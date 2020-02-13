package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;

import java.util.function.Predicate;

public enum TeamRoleAddedFilter implements Predicate<GuildMemberRoleAddEvent> {

    TEAM_ROLE_ADDED_FILTER;

    @Override
    public boolean test(GuildMemberRoleAddEvent guildMemberRoleAddEvent) {
        return guildMemberRoleAddEvent.getRoles().stream().
                map(Role::getName).
                anyMatch(roleName ->
                        "mystic".equals(roleName) || "valor".equals(roleName) || "instinct".equals(roleName));
    }
}
