package net.fender.discord.quartz;

import net.fender.aws.AwsConfiguration;
import net.fender.discord.DiscordConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@Disabled
//@ActiveProfiles("aws-credentials")
@SpringBootTest(webEnvironment = NONE, classes = {
        AwsConfiguration.class,
        DiscordConfiguration.class,
        SilphJob.class
})
@TestPropertySource(properties = {
        "spring.jackson.property-naming-strategy = SNAKE_CASE",
        "spring.jackson.deserialization.fail-on-unknown-properties = false",
        "aws.profile-name = personal"
})
public class SilphJobTest {

    @Autowired
    SilphJob silphJob;

    @Test
    public void test() throws Exception {
        silphJob.execute(null);
    }
}
