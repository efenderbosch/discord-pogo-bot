package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.MemberIsUserFilter;
import net.fender.discord.filters.RegexChannelNameFilter;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TeamRocketRandomEmoteListener extends BaseEventListener<MessageReceivedEvent> {

    private static final RegexChannelNameFilter GENERAL = new RegexChannelNameFilter("general");
    private static final String[] EMOTES = {"team_rocket", "jessie", "james", "giovanni", "rainbow_rocket"};
    private static final Random RANDOM = new Random();

    public TeamRocketRandomEmoteListener() {
        super(MessageReceivedEvent.class, GENERAL, MemberIsUserFilter.INSTANCE);
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {
        int r = RANDOM.nextInt(1000);
        Message message = event.getMessage();
        TextChannel channel = message.getTextChannel();
        if (r == 0) {
            String emoteName = EMOTES[RANDOM.nextInt(EMOTES.length)];
            Emote teamRocket = message.getJDA().getEmotesByName(emoteName, true).get(0);
            message.addReaction(teamRocket).submit();
        } else if (r == 1) {
            channel.sendMessage("Team Rocket captures Pokémon from around the world. They're important tools for " +
                    "keeping our criminal enterprise going. I am the leader, Giovanni!").submit();
        } else if (r == 2) {
            channel.sendMessage("You're just the players in my master plan!").submit();
        } else if (r == 3) {
            channel.sendMessage("♪ There'll be world domination, ♪\n" +
                    "♪ Complete obliteration, ♪\n" +
                    "♪ Of all who now defy me. ♪\n" +
                    "♪ Let the universe prepare, ♪\n" +
                    "♪ Good Pokémon beware, ♪\n" +
                    "♪ You fools shall not deny me! ♪").submit();
        } else if (r == 4) {
            channel.sendMessage("There is nothing I wish to say to you. I will concentrate solely on bettering " +
                    "myself, and none other.").submit();
        }
    }
}
