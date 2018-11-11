package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Message.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Predicate;

import static java.math.RoundingMode.HALF_EVEN;

public class ImageAttachmentStandardSizeFilter implements Predicate<Attachment> {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAttachmentStandardSizeFilter.class);

    private final double min;
    private final double max;

    public ImageAttachmentStandardSizeFilter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean test(Attachment attachment) {
        if (attachment == null) return false;

        try {
            BufferedImage image = ImageIO.read(attachment.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();
            double ratio = 1.0 * width / height;

            if (ratio < min || ratio > max) {
                BigDecimal roundedRatio = BigDecimal.valueOf(ratio).setScale(3, HALF_EVEN);
                LOG.info("non-standard width/height ratio {}/{} = {}", width, height, roundedRatio);
                return false;
            }
            return true;
        } catch (IOException e) {
            LOG.warn("error processing image " + attachment.getId(), e);
        }
        return false;
    }
}
