package net.fender.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.hooks.EventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
public class DiscordConfiguration {

    @Bean
    public JDA jda(DiscordProperties props, List<EventListener> eventListeners)
            throws LoginException, InterruptedException {
        // https://discordapp.com/api/oauth2/authorize?client_id=460973797156061215&permissions=268560464&scope=bot
        return new JDABuilder(AccountType.BOT).
                setGame(Game.playing("Pokemon Go")).
                setToken(props.getToken()).
                addEventListener(eventListeners.toArray()).
                buildBlocking();
    }
}
