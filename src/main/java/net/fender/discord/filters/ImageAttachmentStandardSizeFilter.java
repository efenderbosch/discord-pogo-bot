package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Message.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static java.math.RoundingMode.HALF_EVEN;

public class ImageAttachmentStandardSizeFilter implements Predicate<Attachment> {

    public static final ImageAttachmentStandardSizeFilter STANDARD_SIZE_FILTER = new
            ImageAttachmentStandardSizeFilter(0.45, 0.61);

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
        if (!attachment.isImage()) return false;

        int width = attachment.getWidth();
        int height = attachment.getHeight();
        double ratio = 1.0 * width / height;

        if (ratio < min || ratio > max) {
            BigDecimal roundedRatio = BigDecimal.valueOf(ratio).setScale(3, HALF_EVEN);
            LOG.info("non-standard width/height ratio {}/{} = {}", width, height, roundedRatio);
            return false;
        }

        return true;
    }
}
