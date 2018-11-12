package net.fender.gce.metrics;

import net.fender.EnvironmentUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class VisionAPIRequestCounterTest {

    @Test
    public void test() {
        EnvironmentUtil.addEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", "src/main/resources/gce-credentials" +
                ".json");
        VisionAPIRequestCounter counter = new VisionAPIRequestCounter();
        System.out.println("requesting count");
        long count = counter.count();
        System.out.println("count = " + count).;;
        assertThat(count, greaterThan(0L));
    }
}
