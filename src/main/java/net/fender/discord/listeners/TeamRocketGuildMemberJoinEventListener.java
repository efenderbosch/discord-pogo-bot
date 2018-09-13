package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import org.springframework.stereotype.Component;

@Component
public class TeamRocketGuildMemberJoinEventListener extends BaseEventListener<GuildMemberJoinEvent> {

    public TeamRocketGuildMemberJoinEventListener() {
        super(GuildMemberJoinEvent.class);
    }

    @Override
    protected void processEvent(GuildMemberJoinEvent event) {
        TextChannel generalChannel = event.getJDA().getTextChannelsByName("general", true).get(0);
        generalChannel.sendMessage("Prepare for trouble! " + event.getMember().getAsMention() + " has joined!").submit();
    }
}
