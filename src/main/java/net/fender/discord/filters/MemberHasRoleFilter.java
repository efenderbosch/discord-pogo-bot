package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static net.fender.discord.filters.MemberIsUserFilter.MEMBER_IS_USER_FILTER;

public class MemberHasRoleFilter implements Predicate<MessageReceivedEvent> {

    private final Pattern[] rolePatterns;

    public MemberHasRoleFilter(String... rolePatterns) {
        int length = rolePatterns.length;
        this.rolePatterns = new Pattern[length];
        for (int i = 0; i < length; i++) {
            this.rolePatterns[i] = Pattern.compile(rolePatterns[i]);
        }
    }

    @Override
    public boolean test(MessageReceivedEvent messageReceivedEvent) {
        if (!MEMBER_IS_USER_FILTER.test(messageReceivedEvent)) return false;

        Member member;
        PrivateChannel privateChannel = messageReceivedEvent.getPrivateChannel();
        if (privateChannel != null) {
            User user = privateChannel.getUser();
            Guild guild = user.getMutualGuilds().stream().
                    filter(g -> "Viridian".equalsIgnoreCase(g.getName())).
                    findAny().get();
            member = guild.getMember(user);
        } else {
            member = messageReceivedEvent.getMember();
        }

        List<String> roleNames = member.getRoles().
                stream().
                map(Role::getName).
                collect(toList());

        for (Pattern rolePattern : rolePatterns) {
            for (String roleName : roleNames) {
                if (rolePattern.matcher(roleName).matches()) {
                    return true;
                }
            }
        }
        return false;
    }
}
