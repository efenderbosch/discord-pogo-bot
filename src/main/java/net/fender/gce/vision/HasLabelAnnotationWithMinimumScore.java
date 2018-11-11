package net.fender.gce.vision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;

import java.util.Objects;
import java.util.function.Predicate;

public class HasLabelAnnotationWithMinimumScore implements Predicate<AnnotateImageResponse> {

    public static final HasLabelAnnotationWithMinimumScore IS_SCREENSHOT = new
            HasLabelAnnotationWithMinimumScore(0.5f, "screenshot");

    private final float minimumScore;
    private final String label;

    public HasLabelAnnotationWithMinimumScore(float minimumScore, String label) {
        this.minimumScore = minimumScore;
        this.label = label;
    }

    @Override
    public boolean test(AnnotateImageResponse annotateImageResponse) {
        for (EntityAnnotation annotation : annotateImageResponse.getLabelAnnotationsList()) {
            if (annotation.getScore() > minimumScore && Objects.equals(label, annotation.getDescription())) {
                return true;
            }
        }
        return false;
    }
}
