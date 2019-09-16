package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Settings {

    private int partySize;
    private int maxBuffStages;
    private int buffDivisor;

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public int getMaxBuffStages() {
        return maxBuffStages;
    }

    public void setMaxBuffStages(int maxBuffStages) {
        this.maxBuffStages = maxBuffStages;
    }

    public int getBuffDivisor() {
        return buffDivisor;
    }

    public void setBuffDivisor(int buffDivisor) {
        this.buffDivisor = buffDivisor;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
