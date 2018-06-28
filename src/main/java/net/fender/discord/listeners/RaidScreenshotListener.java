package net.fender.discord.listeners;

import com.google.cloud.vision.v1.*;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.fender.discord.filters.RegexChannelNameFilter;
import net.fender.discord.filters.RegexTextChannelCategoryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.cloud.vision.v1.Feature.Type.DOCUMENT_TEXT_DETECTION;
import static java.util.stream.Collectors.joining;
import static net.fender.discord.filters.ChannelTypeFilter.TEXT_CHANNEL_FILTER;

@Component
public class RaidScreenshotListener extends ListenerAdapter {

    private static final Pattern CURRENT_TIME = Pattern.compile(".*([\\s012][0-9]:[0-5][0-9]).*");
    private static final Pattern REMAINING_TIME = Pattern.compile("([01234]:[0-5][0-9]:[0-5][0-9])");

    private static final RegexTextChannelCategoryFilter RAID_CHANNEL_CATEGORY_FILTER = new RegexTextChannelCategoryFilter("raids");
    private static final RegexChannelNameFilter OCR_BOT_CHANNEL_NAME_FILTER = new RegexChannelNameFilter("ocr-bot");

    private final ImageAnnotatorClient vision;

    @Autowired
    public RaidScreenshotListener(ImageAnnotatorClient imageAnnotatorClient) {
        this.vision = imageAnnotatorClient;
    }

    private static final Logger LOG = LoggerFactory.getLogger(RaidScreenshotListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!TEXT_CHANNEL_FILTER.test(event)) return;
        if (!RAID_CHANNEL_CATEGORY_FILTER.test(event)) return;
        if (!OCR_BOT_CHANNEL_NAME_FILTER.test(event)) return;

        TextChannel textChannel = event.getTextChannel();
        Message message = event.getMessage();
        List<Message.Attachment> attachments = message.getAttachments();

        List<AnnotateImageRequest> requests = new ArrayList<>(attachments.size());
        for (Message.Attachment attachment : attachments) {
            if (!attachment.isImage()) {
                continue;
            }

            LOG.info("attachment {}", attachment.getUrl());
            try (InputStream inputStream = attachment.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(inputStream);
                ByteString imgBytes = ByteString.copyFrom(bytes);
                Image img = Image.newBuilder().setContent(imgBytes).build();
                Feature feat = Feature.newBuilder().setType(DOCUMENT_TEXT_DETECTION).build();
                AnnotateImageRequest request =
                        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                requests.add(request);
            } catch (IOException e) {
                LOG.error("exception processing event " + event.getMessageId(), e);
            }
        }

        BatchAnnotateImagesResponse batchResponse = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = batchResponse.getResponsesList();
        for (AnnotateImageResponse response : responses) {
            if (response.hasError()) {
                LOG.warn("Error: {}", response.getError().getMessage());
                continue;
            }

//            LOG.debug("response: {}", ReflectionToStringBuilder.toString(response));

            String reply = response.getTextAnnotationsList().stream().
                    map(a -> a.getDescription()).
                    collect(joining("\n"));
            textChannel.sendMessage(reply).submit();
        }

//        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
//            if (attachment.isImage()) {
//                LOG.info("attachment {}", attachment.getUrl());
//                try (InputStream inputStream = attachment.getInputStream()) {
//                    ByteBuffer imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
//                    DetectTextRequest request = new DetectTextRequest()
//                            .withImage(new Image().withBytes(imageBytes));
//                    DetectTextResult result = rekognition.detectText(request);
//                    List<TextDetection> textDetections = result.getTextDetections().stream().
//                            filter(t -> t.getType().equals("LINE")).
//                            collect(toList());
//
//                    if (textDetections == null || textDetections.size() < 3) {
//                        LOG.info("not enough text detected");
//                        return;
//                    }
//                    textDetections.forEach(line -> LOG.info("line: {}", line));
//
//                    TextDetection top = textDetections.get(0);
//                    LOG.info("top line: {}", top.getDetectedText());
//                    Matcher currentTimeMatcher = CURRENT_TIME.matcher(top.getDetectedText());
//
//                    String currentTime = currentTimeMatcher.matches() ? currentTimeMatcher.group(1).trim() :
// "unknown";
//                    if (currentTime.length() == 4) {
//                        currentTime = "0" + currentTime;
//                    }
//                    // TODO look for seconds, assume 30 if not present
//                    LocalTime time = LocalTime.parse(currentTime).plusSeconds(30);
//
//                    String location = textDetections.get(1).getDetectedText();
//
//                    String detectedRemainingTime = textDetections.get(2).getDetectedText();
//                    LOG.info("remaining time: {}", detectedRemainingTime);
//                    Matcher remainingTimeMatcher = REMAINING_TIME.matcher(detectedRemainingTime);
//                    String remainingTime = remainingTimeMatcher.matches() ? remainingTimeMatcher.group() : "unknown";
//
//                    String[] remainingTimeParts = remainingTime.split(":");
//                    long remainingHours = Long.parseLong(remainingTimeParts[0]);
//                    long remainingMinutes = Long.parseLong(remainingTimeParts[1]);
//                    long remainingSeconds = Long.parseLong(remainingTimeParts[2]);
//                    LocalTime hatchTime = time.
//                            plusHours(remainingHours).
//                            plusMinutes(remainingMinutes).
//                            plusSeconds(remainingSeconds);
//
//                    StringBuilder reply = new StringBuilder();
//                    reply.append("current time: ").append(time.truncatedTo(MINUTES)).append('\n');
//                    reply.append("location: ").append(location).append('\n');
//                    reply.append("remaining time: ").append(remainingTime).append('\n');
//                    reply.append("hatch time: ").append(hatchTime.truncatedTo(MINUTES));
//                    messageChannel.sendMessage(reply.toString()).submit();
//                } catch (Exception e) {
//                    LOG.error("error", e);
//                }
    }
}

