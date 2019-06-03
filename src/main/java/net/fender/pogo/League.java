package net.fender.pogo;

import java.util.Optional;

public enum League {

    GREAT(1500), ULTRA(2500), MASTER(9999);

    public final int maxCp;

    League(int maxCp) {
        this.maxCp = maxCp;
    }

    public static Optional<League> find(String name) {
        if (GREAT.name().equalsIgnoreCase(name)) return Optional.of(GREAT);
        if (ULTRA.name().equalsIgnoreCase(name)) return Optional.of(ULTRA);
        if (MASTER.name().equalsIgnoreCase(name)) return Optional.of(MASTER);
        return Optional.empty();
    }
}
