package net.fender.gce;

import com.google.cloud.vision.v1.ImageAnnotatorClient;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

//@Configuration
public class GceConfiguration {

    @Bean
    public ImageAnnotatorClient imageAnnotatorClient() throws IOException {
        return ImageAnnotatorClient.create();
    }
}
