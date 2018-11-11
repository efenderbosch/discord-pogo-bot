package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

import java.util.function.Predicate;

public enum MemberIsBotFilter implements Predicate<GenericMessageReactionEvent> {

    MEMBER_IS_BOT_FILTER;

    @Override
    public boolean test(GenericMessageReactionEvent event) {
        if (event == null) return false;

        Member member = event.getMember();
        if (member == null) return false;

        User user = member.getUser();
        if (user == null) return false;

        return user.isBot();
    }
}
