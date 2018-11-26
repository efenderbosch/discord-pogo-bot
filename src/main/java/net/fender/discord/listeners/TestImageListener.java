package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.rekognition.model.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.fender.discord.filters.HasImageAttachmentFilter.HAS_IMAGE_ATTACHMENT_FILTER;
import static net.fender.discord.filters.MemberIsUserFilter.MEMBER_IS_USER_FILTER;

@Component
public class TestImageListener extends BaseEventListener<MessageReceivedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(TestImageListener.class);

    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            ("test-images");

    private final RekognitionClient rekognitionClient;
    private TextChannel testImagesChannel;

    @Autowired
    public TestImageListener(RekognitionClient rekognitionClient) {
        super(MessageReceivedEvent.class, MEMBER_IS_USER_FILTER, CHANNEL_NAME_FILTER, HAS_IMAGE_ATTACHMENT_FILTER);
        this.rekognitionClient = rekognitionClient;
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {
        if (testImagesChannel == null) {
            JDA jda = event.getJDA();
            testImagesChannel = jda.getTextChannelsByName("test-images", true).get(0);
        }
        testImagesChannel.sendTyping().submit();

        Message message = event.getMessage();

        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        if (imageAttachments.size() > 1) {
            testImagesChannel.sendMessage("I can't scan multiple images in the same post, yet.").submit();
            return;
        }

        Attachment attachment = imageAttachments.get(0);
        SdkBytes sdkBytes;
        BufferedImage bufferedImage;
        try {
            sdkBytes = SdkBytes.fromInputStream(attachment.getInputStream());
            bufferedImage = ImageIO.read(attachment.getInputStream());
        } catch (IOException e) {
            LOG.error("exception getting attachment", e);
            testImagesChannel.sendMessage("exception getting attachment " + e.getMessage()).submit();
            return;
        }
        Image image = Image.builder().bytes(sdkBytes).build();
        DetectTextRequest request = DetectTextRequest.builder().image(image).build();
        DetectTextResponse response = rekognitionClient.detectText(request);

        int width = attachment.getWidth();
        int height = attachment.getHeight();
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        Stroke wide = new BasicStroke(8);
        Stroke narrow = new BasicStroke(4);


        String pokestop = null;
        for (TextDetection textDetection : response.textDetections()) {
            if (textDetection.type() != TextTypes.LINE) continue;

            float top = textDetection.geometry().boundingBox().top();
            if (top < 0.05 || top > 0.1) continue;

            pokestop = textDetection.detectedText();
            break;
        }
        for (TextDetection textDetection : response.textDetections()) {
            TextTypes type = textDetection.type();
            String text = textDetection.detectedText();
            BoundingBox boundingBox = textDetection.geometry().boundingBox();
            switch (type) {
                case LINE:
                    testImagesChannel.sendMessage(text).submit();
                    graphics2D.setColor(Color.YELLOW);
                    graphics2D.setStroke(wide);
                    float top = textDetection.geometry().boundingBox().top();
                    if (top >= 0.05 && top <= 0.1) {
                        pokestop = textDetection.detectedText();
                    }
                    break;
                case WORD:
                    graphics2D.setColor(Color.GREEN);
                    graphics2D.setStroke(narrow);
                    break;
            }
            drawBoundingBox(width, height, graphics2D, boundingBox);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            testImagesChannel.sendFile(baos.toByteArray(), attachment.getFileName()).submit();
        } catch (IOException e) {
            LOG.error("error writing image", e);
            testImagesChannel.sendMessage("error writing image " + e.getMessage()).submit();
        }
    }

    private void drawBoundingBox(int width, int height, Graphics2D graphics2D, BoundingBox boundingBox) {
        int x = (int) (width * boundingBox.left());
        int y = (int) (height * boundingBox.top());
        int w = (int) (width * boundingBox.width());
        int h = (int) (height * boundingBox.height());
        graphics2D.drawRect(x, y, w, h);
    }
}
