package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TeamRocketGuildMemberJoinEventListener extends BaseEventListener<GuildMemberJoinEvent> {

    public TeamRocketGuildMemberJoinEventListener() {
        super(GuildMemberJoinEvent.class);
    }

    private static final String[] GREETINGS = {
            "So! I must say, I am impressed you got here, TRAINER. Team Rocket captures Pokémon from around the " +
                    "world. They're important tools for keeping our criminal enterprise going. I am the leader, " +
                    "Giovanni!",
            "There is nothing I wish to say to you, TRAINER. I will concentrate solely on bettering myself, and none" +
                    " other."};
    private static final Random RANDOM = new Random();

    @Override
    protected void processEvent(GuildMemberJoinEvent event) {
        TextChannel generalChannel = event.getJDA().getTextChannelsByName("general", true).get(0);
        String greeting = GREETINGS[RANDOM.nextInt(GREETINGS.length)];
        greeting = greeting.replace("TRAINER", event.getMember().getAsMention());
        generalChannel.sendMessage(greeting).submit();
    }
}
