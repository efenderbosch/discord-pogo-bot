package net.fender.pogo;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public enum Team {

    VALOR("Valor", "valor"),
    MYSTIC("Mystic", "mystic"),
    INSTINCT("Instinct", "instinct"),
    HARMONY("Harmony", "harmony");

    public static final Set<String> TEAM_ROLES = stream(values()).map(team -> team.roleName).collect(toSet());

    public final String display;
    public final String roleName;

    Team(String display, String roleName) {
        this.display = display;
        this.roleName = roleName;
    }

    public static Optional<Team> getTeam(String team) {
        if (team == null) return Optional.empty();

        try {
            return Optional.of(valueOf(team.toUpperCase(Locale.getDefault()).trim()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
