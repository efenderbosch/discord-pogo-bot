package net.fender.discord.quartz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.fender.discord.DiscordConfiguration;
import org.junit.jupiter.api.Disabled;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
public class PurgeChannelJobTest {

    @Autowired
    ResourceLoader resourceLoader;

    @Disabled
    public void purge_quests() throws Exception {
        Resource resource = resourceLoader.getResource("application-aws-credentials.yml");

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = mapper.readTree(resource.getInputStream());
        JsonNode aws = root.get("aws");
        String accessKeyId = aws.get("access-key-id").asText();
        String secretAccessKey = aws.get("secret-key").asText();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        SsmClient ssmClient = SsmClient.builder().credentialsProvider(credentialsProvider).build();
        String token = DiscordConfiguration.getDiscordToken(ssmClient);

        JDA jda = new JDABuilder(AccountType.BOT).
                setGame(Game.playing("Pokemon Go")).
                setToken(token).
                buildBlocking();

        PurgeChannelJob job = new PurgeChannelJob();
        job.setJda(jda);

        JobExecutionContext context = mock(JobExecutionContext.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("channel", "team-rocket-leaders");
        jobDataMap.put("truncateTo", ChronoUnit.DAYS);
        jobDataMap.put("window", Duration.ofMinutes(1));
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
        job.execute(context);

        Thread.sleep(15_000);
        jda.shutdown();
    }
}
