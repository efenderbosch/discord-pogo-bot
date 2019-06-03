package net.fender.pogo;

public enum League {

    GREAT(1500), ULTRA(2500), MASTER(9999);

    public final int maxCp;

    League(int maxCp) {
        this.maxCp = maxCp;
    }
}
