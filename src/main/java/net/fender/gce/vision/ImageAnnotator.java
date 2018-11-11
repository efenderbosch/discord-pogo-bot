package net.fender.gce.vision;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import net.dv8tion.jda.core.entities.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.google.cloud.vision.v1.Feature.Type.DOCUMENT_TEXT_DETECTION;
import static com.google.cloud.vision.v1.Feature.Type.LABEL_DETECTION;

@Component
public class ImageAnnotator {

    private static final Feature LABEL_DETECTION_FEATURE = Feature.newBuilder().setType(LABEL_DETECTION).build();
    private static final Feature TEXT_DETECTION_FEATURE = Feature.newBuilder().setType(DOCUMENT_TEXT_DETECTION).build();

    public AnnotateImageResponse annotate(Message.Attachment attachment) throws IOException {
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            return annotate(attachment, vision);
        }
    }

    private AnnotateImageResponse annotate(Message.Attachment attachment, ImageAnnotatorClient vision) throws IOException {
        ByteString bytes = ByteString.readFrom(attachment.getInputStream());
        Image img = Image.newBuilder().setContent(bytes).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().
                addFeatures(LABEL_DETECTION_FEATURE).
                addFeatures(TEXT_DETECTION_FEATURE).
                setImage(img).
                build();
        List<AnnotateImageRequest> requests = Collections.singletonList(request);
        BatchAnnotateImagesResponse responses = vision.batchAnnotateImages(requests);
        return responses.getResponses(0);
    }
}

//        if (annotateImageResponse.hasError()) {
//            Status error = annotateImageResponse.getError();
//            LOG.warn("GCE Vision error {} {}", error.getCode(), error.getMessage());
//        }
//
//        boolean screenshot = false;
//        MessageBuilder labelMessageBuilder = new MessageBuilder();
//        for (EntityAnnotation annotation : annotateImageResponse.getLabelAnnotationsList()) {
//            LOG.info("label: {}; score: {}", annotation.getDescription(), annotation.getScore());
//            labelMessageBuilder.append("label: ").append(annotation.getDescription());
//            labelMessageBuilder.append("; score: ").append(annotation.getScore()).append('\n');
//            if (annotation.getScore() > 0.5 && "screenshot".equals(annotation.getDescription())) {
//                screenshot = true;
//            }
//        }
//        response.setScreenshot(screenshot);
//
//        boolean ivScreenshot = false;
//        boolean hasWeight = false;
//        boolean hasHeight = false;
//        boolean hasCandy = false;
//        boolean hasStardust = false;
//        boolean hasField = false;
//        boolean hasSpecial = false;
//        MessageBuilder textMessageBuilder = new MessageBuilder();
//        for (EntityAnnotation annotation : annotateImageResponse.getTextAnnotationsList()) {
//            String description = annotation.getDescription();
//            if (description.contains("Cost to reach Lvl") ||
//                    description.contains("VIEW IN POKE GENIE") ||
//                    description.contains("Perfect IV") ||
//                    description.contains("Excellent IV") ||
//                    description.contains("POKEMON INFO")) {
//                ivScreenshot = true;
//            }
//            if ("WEIGHT".equals(description)) {
//                hasWeight = true;
//            }
//            if ("HEIGHT".equals(description)) {
//                hasHeight = true;
//            }
//            if ("CANDY".equals(description)) {
//                hasCandy = true;
//            }
//            if ("STARDUST".equals(description)) {
//                hasStardust = true;
//            }
//            if ("FIELD".equals(description)) {
//                hasField = true;
//            }
//            if ("SPECIAL".equals(description)) {
//                hasSpecial = true;
//            }
//            LOG.info("text: {}", description);
//            textMessageBuilder.append("text: ").append(description).append('\n');
//        }
//        response.setIvScreenshot(ivScreenshot);
//        response.setPokemonScreenshot(hasWeight && hasHeight && hasCandy && hasStardust);
//        response.setQuestScreenshot(hasField && hasSpecial);
//
//        return response;
//    }

// ME && FRIENDS
// PARTY && POKEMON && EGGS