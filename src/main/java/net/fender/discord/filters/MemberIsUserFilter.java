package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.function.Predicate;

public enum MemberIsUserFilter implements Predicate<MessageReceivedEvent> {

    MEMBER_IS_USER_FILTER;

    @Override
    public boolean test(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent == null) return false;

        Message message = messageReceivedEvent.getMessage();
        if (message == null) return false;

        PrivateChannel privateChannel = message.getPrivateChannel();
        if (privateChannel != null) {
            User user = privateChannel.getUser();
            return !user.isBot();
        }

        Member member = message.getMember();
        if (member == null) return false;

        User user = member.getUser();
        if (user == null) return false;

        return !user.isBot();
    }
}
