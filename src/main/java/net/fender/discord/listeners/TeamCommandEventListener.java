package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.fender.pogo.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toSet;
import static net.fender.pogo.Team.TEAM_ROLES;

@Component
public class TeamCommandEventListener extends CommandEventListener {

    public TeamCommandEventListener() {
        super(Pattern.compile("^\\$team (.*)$"));
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        JDA jda = event.getJDA();
        TextChannel textChannel = event.getTextChannel();

        String arg = parts.get(1);
        Optional<Team> maybeTeam = Team.getTeam(arg);
        Member member = event.getMember();

        if (!maybeTeam.isPresent()) {
            Message reply = new MessageBuilder().
                    append(member).
                    append(" there's no team ").
                    append(arg).
                    append(". Use valor, mystic, instinct or harmony.").
                    build();
            textChannel.sendMessage(reply).submit();
            return;
        }

        Team team = maybeTeam.get();
        Set<String> memberRoles = member.getRoles().stream().map(Role::getName).collect(toSet());
        boolean removed = memberRoles.removeAll(TEAM_ROLES);
        if (removed) {
            Message reply = new MessageBuilder().
                    append(member).
                    append(" you already have a team role!").
                    build();
            textChannel.sendMessage(reply).submit();
            return;
        }

        List<Role> roles = jda.getRolesByName(team.roleName, true);
        if (roles.isEmpty()) {
            Message reply = new MessageBuilder().
                    append("team role ").
                    append(team.roleName).
                    append(" does not exist. Please let an admin know.").
                    build();
            textChannel.sendMessage(reply).submit();
            return;
        }

        Role role = roles.get(0);
        try {
            member.getGuild().getController().addSingleRoleToMember(member, role).submit();
        } catch (HierarchyException e) {
            Message reply = new MessageBuilder().
                    append("Can't assign ").
                    append(member).
                    append(" to team ").
                    append(team.display).
                    append(". My role must be higher than the team roles.").
                    build();
            textChannel.sendMessage(reply).submit();
            return;
        }

        MessageBuilder builder = new MessageBuilder().
                append("Added ").
                append(member).
                append(" to team ").
                append(team.display);
        List<Emote> teamEmotes = jda.getEmotesByName(team.display, true);
        if (!teamEmotes.isEmpty()) {
            builder.append(' ').append(teamEmotes.get(0));
        }
        Message reply = builder.build();
        textChannel.sendMessage(reply).submit();
    }
}
