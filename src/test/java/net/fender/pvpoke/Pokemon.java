package net.fender.pvpoke;

import net.fender.pogo.BaseStats;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pokemon {

    private int dex;
    private String speciesName;
    private String speciesId;
    private BaseStats baseStats;
    private List<String> types;
    private List<String> fastMoves;
    private List<String> chargeMoves;
    private Set<String> legacyMoves = new HashSet<>();
    // defaultIVs

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public BaseStats getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(BaseStats baseStats) {
        this.baseStats = baseStats;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getFastMoves() {
        return fastMoves;
    }

    public void setFastMoves(List<String> fastMoves) {
        this.fastMoves = fastMoves;
    }

    public List<String> getChargeMoves() {
        return chargeMoves;
    }

    public void setChargeMoves(List<String> chargeMoves) {
        this.chargeMoves = chargeMoves;
    }

    public Set<String> getLegacyMoves() {
        return legacyMoves;
    }

    public void setLegacyMoves(Set<String> legacyMoves) {
        this.legacyMoves = legacyMoves;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
