package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class MemberHasRoleFilter implements EventFilter<MessageReceivedEvent> {

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
        if (!MemberIsUserFilter.INSTANCE.test(messageReceivedEvent)) return false;

        List<String> roleNames = messageReceivedEvent.
                getMember().
                getRoles().
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
