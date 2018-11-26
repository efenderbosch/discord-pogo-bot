package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.discord.filters.TimeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;
import static net.fender.discord.filters.HasImageAttachmentFilter.HAS_IMAGE_ATTACHMENT_FILTER;

@Component
public class FacebookImageListener extends BaseEventListener<MessageReceivedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(FacebookImageListener.class);

    private static final LocalTime START = LocalTime.of(5, 45);
    private static final LocalTime END = LocalTime.of(19, 15);
    private static final TimeFilter RAID_WINDOW_FILTER = new TimeFilter(START, END);

    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            ("west-side-pokemon-go", "downtown-akron-pokemon-go");

    private final RekognitionClient rekognition;
    private TextChannel announceRaidsHere;
    private TextChannel badImages;

    @Autowired
    public FacebookImageListener(RekognitionClient rekognition) {
        super(MessageReceivedEvent.class, RAID_WINDOW_FILTER, CHANNEL_NAME_FILTER, HAS_IMAGE_ATTACHMENT_FILTER);
        this.rekognition = rekognition;
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
        // Barb Hale
        String author = message.getAuthor().getName();
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
            int width = attachment.getWidth();
            int height = attachment.getHeight();
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
        SdkBytes sdkBytes;
        try {
            sdkBytes = SdkBytes.fromInputStream(attachment.getInputStream());
        } catch (IOException e) {
            LOG.error("exception getting attachment", e);
            badImages.sendMessage("exception getting attachment " + e.getMessage()).submit();
            return;
        }

        Image image = Image.builder().bytes(sdkBytes).build();
        DetectTextRequest request = DetectTextRequest.builder().image(image).build();
        DetectTextResponse response = rekognition.detectText(request);

        boolean isIvScreenshot = false;
        boolean hasWeight = false;
        boolean hasHeight = false;
        boolean hasCandy = false;
        boolean hasStardust = false;
        for (TextDetection textDetection : response.textDetections()) {
            if (textDetection.type() != TextTypes.LINE) continue;

            String text = textDetection.detectedText();
            if (text.contains("Cost to reach Lvl") || text.contains("VIEW IN POKE GENIE")) {
                isIvScreenshot = true;
            }
            if (text.contains("WEIGHT")) {
                hasWeight = true;
            }
            if (text.contains("HEIGHT")) {
                hasHeight = true;
            }
            if (text.contains("CANDY")) {
                hasCandy = true;
            }
            if (text.contains("STARDUST")) {
                hasStardust = true;
            }

            boolean pokemonScreenshot = hasWeight && hasHeight && hasCandy && hasStardust;

            if (isIvScreenshot) {
                try {
                    badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                } catch (IOException e) {
                    LOG.error("exception sending attachment to #bad-images", e);
                }
                badImages.sendMessage("IV Screenshot, ignoring").submit();
                return;
            }

            if (pokemonScreenshot) {
                try {
                    badImages.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
                } catch (IOException e) {
                    LOG.error("exception sending attachment to #bad-images", e);
                }
                badImages.sendMessage("Pokemon Screenshot, ignoring").submit();
                return;
            }
        }

        LOG.info("passed screenshot test, sending to pokenav");
        try {
            announceRaidsHere.sendFile(attachment.getInputStream(), attachment.getFileName()).submit();
        } catch (IOException e) {
            LOG.error("exception sending attachment to #announce-raids-here", e);
        }
    }
}
