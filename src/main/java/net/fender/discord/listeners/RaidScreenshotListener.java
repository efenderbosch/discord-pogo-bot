package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;

@Component
public class RaidScreenshotListener extends BaseEventListener<MessageReceivedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(RaidScreenshotListener.class);

    private static final LocalTime START = LocalTime.of(6, 0);
    private static final LocalTime END = LocalTime.of(19, 15);
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            ("west-side-pokemon-go", "downtown-akron-pokemon-go");

    public RaidScreenshotListener() {
        super(MessageReceivedEvent.class, CHANNEL_NAME_FILTER);
    }

    @Override
    public void processEvent(MessageReceivedEvent event) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(START)) return;
        if (now.isAfter(END)) return;

        Message message = event.getMessage();
        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        if (imageAttachments.isEmpty()) return;

        JDA jda = message.getJDA();

        TextChannel announceRaidsHere = jda.getTextChannelsByName("announce-raids-here", true).get(0);
        TextChannel badImages = jda.getTextChannelsByName("bad-images", true).get(0);
        for (Attachment attachment : imageAttachments) {
            LOG.info("got facebook image {}", attachment.getId());

            try {
                InputStream inputStream = attachment.getInputStream();
                BufferedImage image = ImageIO.read(attachment.getInputStream());
                // attachment input stream is a ByteArrayInputStream which supports mark/reset
                inputStream.mark(0);
                inputStream.reset();

                int width = image.getWidth();
                int height = image.getHeight();
                double ratio = 1.0 * width / height;
                BigDecimal roundedRatio = BigDecimal.valueOf(ratio).setScale(2, HALF_EVEN);

                TextChannel receivingChannel;
                if (ratio < 0.45 || ratio > 0.57) {
                    LOG.info("non-standard width/height ratio {}/{} = {}; assuming not a screenshot", width, height,
                            roundedRatio);
                    receivingChannel = badImages;
                } else {
                    LOG.info("standard width/height ratio {}/{} = {}; sending to pokenav", width, height, roundedRatio);
                    receivingChannel = announceRaidsHere;
                }
                receivingChannel.sendFile(inputStream, attachment.getFileName()).submit();
            } catch (IOException e) {
                LOG.warn("error mirroring {}", attachment.getId());
            }
        }
    }
}
