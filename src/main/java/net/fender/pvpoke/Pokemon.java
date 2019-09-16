package net.fender.pvpoke;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pokemon {

    private int dex;
    private String speciesId;
    private BaseStats baseStats = new BaseStats(0, 0, 0);
    private Set<String> types = new HashSet<>();
    private List<String> fastMoves = new ArrayList<>();
    private List<String> chargeMoves = new ArrayList<>();
    private Set<String> legacyMoves = new HashSet<>();
    private Set<String> tags = new HashSet<>();
    // defaultIVs

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
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

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
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

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public boolean isTradable() {
        return !tags.contains("mythical") || dex == 808 || dex == 809;
    }

    @JsonIgnore
    public int getLevelFloor() {
        if ((tags.contains("legendary") || tags.contains("mythical")) && dex != 808 & dex != 809) {
            return 15;
        }
        return 1;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
