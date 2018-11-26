package net.fender.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
public class DiscordConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordConfiguration.class);

    @Bean
    public JDA jda(SsmClient ssmClient,
                   List<EventListener> eventListeners)
            throws LoginException, InterruptedException {
        String token = getDiscordToken(ssmClient);
        // https://discordapp.com/api/oauth2/authorize?client_id=460973797156061215&permissions=268560464&scope=bot
        JDA jda = new JDABuilder(AccountType.BOT).
                setGame(Game.playing("Pokemon Go")).
                setToken(token).
                addEventListener(eventListeners.toArray()).
                buildBlocking();
        LOG.info("JDA status: {}", jda.getStatus());
        return jda;
    }

    public static String getDiscordToken(SsmClient ssmClient) {
        GetParameterRequest request = GetParameterRequest.builder().
                name("/discord/token").
                withDecryption(Boolean.TRUE).
                build();
        GetParameterResponse response = ssmClient.getParameter(request);
        return response.parameter().value();
    }
}
