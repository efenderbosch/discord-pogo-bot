package net.fender.pogo;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum RaidLevel {

    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    EX("X"),
    UNKNOWN("?");

    public final String level;

    public static final Map<String, RaidLevel> RAIDS_BY_LEVEL = stream(values()).collect(toMap((v -> v.level),
            identity()));

    RaidLevel(String level) {
        this.level = level;
    }
}
