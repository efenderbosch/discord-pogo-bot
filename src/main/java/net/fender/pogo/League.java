package net.fender.pogo;

import java.util.Optional;

public enum League {

    great(1500), ultra(2500), master(9999);

    public final int maxCp;

    League(int maxCp) {
        this.maxCp = maxCp;
    }

    public static Optional<League> find(String name) {
        if (great.name().equalsIgnoreCase(name)) return Optional.of(great);
        if (ultra.name().equalsIgnoreCase(name)) return Optional.of(ultra);
        if (master.name().equalsIgnoreCase(name)) return Optional.of(master);
        return Optional.empty();
    }
}
