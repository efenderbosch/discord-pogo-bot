package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.RegexChannelNameFilter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TeamRocketRandomEmoteListener extends BaseEventListener<MessageReceivedEvent> {

    private static final RegexChannelNameFilter GENERAL  = new RegexChannelNameFilter("after-hours");
    private static final String[] EMOTES = {"team_rocket", "jessie", "james"};
    private static final Random random = new Random();

    public TeamRocketRandomEmoteListener() {
        super(MessageReceivedEvent.class, GENERAL);
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {
        if (random.nextInt(1000) == 0) {
            Message message = event.getMessage();
            String emoteName = EMOTES[random.nextInt(3)];
            Emote teamRocket = message.getJDA().getEmotesByName(emoteName, true).get(0);
            message.addReaction(teamRocket).submit();
        }
    }
}
