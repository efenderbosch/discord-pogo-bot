package net.fender.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
@EnableConfigurationProperties(DiscordProperties.class)
public class DiscordConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordConfiguration.class);

    @Bean
    public JDA jda(DiscordProperties props, List<EventListener> eventListeners)
            throws LoginException, InterruptedException {
        LOG.info("discord props: {}", props);
        // https://discordapp.com/api/oauth2/authorize?client_id=460973797156061215&permissions=268560464&scope=bot
        JDA jda = new JDABuilder(AccountType.BOT).
                setGame(Game.playing("Pokemon Go")).
                setToken(props.getToken()).
                addEventListener(eventListeners.toArray()).
                buildBlocking();
        LOG.info("JDA loaded {}", jda.getStatus());
        return jda;
    }
}
