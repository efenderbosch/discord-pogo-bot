package net.fender.discord.listeners;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.google.rpc.Status;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.discord.filters.TimeFilter;
import net.fender.gce.vision.ImageAnnotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static com.google.cloud.vision.v1.Feature.Type.DOCUMENT_TEXT_DETECTION;
import static com.google.cloud.vision.v1.Feature.Type.LABEL_DETECTION;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;
import static net.fender.discord.filters.HasImageAttachmentFilter.HAS_IMAGE_ATTACHMENT_FILTER;

@Component
public class RaidScreenshotListener extends BaseEventListener<MessageReceivedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(RaidScreenshotListener.class);

    private static final LocalTime START = LocalTime.of(5, 45);
    private static final LocalTime END = LocalTime.of(19, 15);
    private static final TimeFilter RAID_WINDOW_FILTER = new TimeFilter(START, END);

    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            ("west-side-pokemon-go", "downtown-akron-pokemon-go");

    private final ImageAnnotator imageAnnotator;
    private TextChannel announceRaidsHere;
    private TextChannel badImages;

    @Autowired
    public RaidScreenshotListener(ImageAnnotator imageAnnotator) {
        super(MessageReceivedEvent.class, RAID_WINDOW_FILTER, CHANNEL_NAME_FILTER, HAS_IMAGE_ATTACHMENT_FILTER);
        this.imageAnnotator = imageAnnotator;
    }

    @Override
    public void processEvent(MessageReceivedEvent event) {

        if (announceRaidsHere == null || badImages == null) {
            JDA jda = event.getJDA();
            announceRaidsHere = jda.getTextChannelsByName("announce-raids-here", true).get(0);
            badImages = jda.getTextChannelsByName("bad-images", true).get(0);
        }

        Message message = event.getMessage();
        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        for (Attachment attachment : imageAttachments) {
            processAttachment(attachment);
        }
    }

    private void processAttachment(Attachment attachment) {
        LOG.info("facebook image file name {}", attachment.getFileName());
        LOG.info("facebook image URL {}", attachment.getUrl());

        if (!isStandardWidthHeight(attachment)) return;

        checkValidScreenshot(attachment);
    }

    private boolean isStandardWidthHeight(Attachment attachment) {
        try {
            BufferedImage image = ImageIO.read(attachment.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();
            double ratio = 1.0 * width / height;
            BigDecimal roundedRatio = BigDecimal.valueOf(ratio).setScale(2, HALF_EVEN);

            if (ratio < 0.45 || ratio > 0.61) {
                LOG.info("non-standard width/height ratio {}/{} = {}; assuming not a screenshot", width, height,
                        roundedRatio);
                String message = "non standard width/height ratio. " + width + "/" + height + " = " + roundedRatio;
                badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                badImages.sendMessage(message).submit();

                return false;
            }
            return true;
        } catch (IOException e) {
            LOG.warn("error processing image " + attachment.getId(), e);
        }
        return false;
    }

    private void checkValidScreenshot(Attachment attachment) {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            ByteString bytes = ByteString.readFrom(attachment.getInputStream());
            Image img = Image.newBuilder().setContent(bytes).build();
            Feature labelFeature = Feature.newBuilder().setType(LABEL_DETECTION).build();
            Feature textFeature = Feature.newBuilder().setType(DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().
                    setImage(img).
                    addFeatures(labelFeature).
                    addFeatures(textFeature).
                    build();
            List<AnnotateImageRequest> requests = Collections.singletonList(request);
            BatchAnnotateImagesResponse responses = vision.batchAnnotateImages(requests);
            AnnotateImageResponse response = responses.getResponsesList().get(0);
            if (response.hasError()) {
                Status error = response.getError();
                LOG.warn("GCE Vision error {} {}", error.getCode(), error.getMessage());
                badImages.sendMessage("GCE Vision error " + error.getCode() + " " + error.getMessage());
            }

            boolean screenshot = false;
            MessageBuilder labelMessageBuilder = new MessageBuilder();
            for (EntityAnnotation annotation : response.getLabelAnnotationsList()) {
                LOG.info("label: {}; score: {}", annotation.getDescription(), annotation.getScore());
                labelMessageBuilder.append("label: ").append(annotation.getDescription());
                labelMessageBuilder.append("; score: ").append(annotation.getScore()).append('\n');
                if (annotation.getScore() > 0.5 && "screenshot".equals(annotation.getDescription())) {
                    screenshot = true;
                }
            }
            Message labelMessage = labelMessageBuilder.build();

            boolean isIvScreenshot = false;
            boolean hasWeight = false;
            boolean hasHeight = false;
            boolean hasCandy = false;
            boolean hasStardust = false;
            MessageBuilder textMessageBuilder = new MessageBuilder();
            List<EntityAnnotation> textAnnotationsList = response.getTextAnnotationsList();
            EntityAnnotation firstAnntation = textAnnotationsList.get(0);
            String fullDescription = firstAnntation.getDescription();
            LOG.info("text: {}", fullDescription);
            textMessageBuilder.append("text: ").append(fullDescription).append('\n');
            for (EntityAnnotation annotation : textAnnotationsList) {
                String description = annotation.getDescription();
                if (description.contains("Cost to reach Lvl") || description.contains("VIEW IN POKE GENIE")) {
                    isIvScreenshot = true;
                }
                if ("WEIGHT".equals(description)) {
                    hasWeight = true;
                }
                if ("HEIGHT".equals(description)) {
                    hasHeight = true;
                }
                if ("CANDY".equals(description)) {
                    hasCandy = true;
                }
                if ("STARDUST".equals(description)) {
                    hasStardust = true;
                }
            }
            if (textMessageBuilder.length() > 2000) {
                // TODO
            }
            Message textMessage = textMessageBuilder.build();

            boolean pokemonScreenshot = hasWeight && hasHeight && hasCandy && hasStardust;

            if (!screenshot) {
                badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                badImages.sendMessage(labelMessage).submit();
                badImages.sendMessage(textMessage).submit();
                return;
            }

            if (isIvScreenshot) {
                badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                badImages.sendMessage("IV Screenshot, ignoring").submit();
                return;
            }

            if (pokemonScreenshot) {
                badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                badImages.sendMessage("Pokemon Screenshot, ignoring").submit();
                return;
            }

            LOG.info("passed screenshot test, sending to pokenav");
            announceRaidsHere.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
        } catch (IOException e) {
            LOG.warn("error processing image " + attachment.getId(), e);
        }
    }
}
