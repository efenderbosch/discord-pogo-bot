package net.fender.gce.vision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;

import java.util.Objects;
import java.util.function.Predicate;

public class HasTextAnnotation implements Predicate<AnnotateImageResponse> {

    public static final Predicate<AnnotateImageResponse> IS_POKEMON_SCREENSHOT = HasTextAnnotation.allOf("HEIGHT",
            "WIDTH", "STARTDUST", "CANDY");

    public static final Predicate<AnnotateImageResponse> IS_IV_SCREENSHOT = HasTextAnnotation.anyOf("VIEW IN POKE " +
            "GENIE", "Cost to reach Lvl");

    public static Predicate<AnnotateImageResponse> allOf(String... searchDescription) {
        Predicate<AnnotateImageResponse> predicate = new HasTextAnnotation(searchDescription[0]);
        for (int i = 1; i < searchDescription.length; i++) {
            predicate = predicate.and(new HasTextAnnotation(searchDescription[i]));
        }
        return predicate;
    }

    public static Predicate<AnnotateImageResponse> anyOf(String... searchDescription) {
        Predicate<AnnotateImageResponse> predicate = new HasTextAnnotation(searchDescription[0]);
        for (int i = 1; i < searchDescription.length; i++) {
            predicate = predicate.or(new HasTextAnnotation(searchDescription[i]));
        }
        return predicate;
    }

    private final String searchDescription;

    public HasTextAnnotation(String searchDescription) {
        this.searchDescription = searchDescription;
    }

    @Override
    public boolean test(AnnotateImageResponse annotateImageResponse) {
        for (EntityAnnotation annotation : annotateImageResponse.getTextAnnotationsList()) {
            String description = annotation.getDescription();
            if (Objects.equals(searchDescription, description)) {
                return true;
            }
        }
        return false;
    }
}
