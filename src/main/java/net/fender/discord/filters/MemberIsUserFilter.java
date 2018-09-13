package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public enum MemberIsUserFilter implements EventFilter<MessageReceivedEvent> {

    INSTANCE;

    @Override
    public boolean test(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent == null) return false;

        Message message = messageReceivedEvent.getMessage();
        if (message == null) return false;

        Member member = message.getMember();
        if (member == null) return false;

        User user = member.getUser();
        if (user == null) return false;

        return !user.isBot();
    }
}
