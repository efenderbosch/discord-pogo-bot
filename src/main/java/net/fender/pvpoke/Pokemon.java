package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;

public class Pokemon {

    private int dex;
    private String speciesId;
    private BaseStats baseStats;
    private Set<String> types = new HashSet<>();
    private List<String> fastMoves = new ArrayList<>();
    private List<String> chargeMoves = new ArrayList<>();
    private Set<String> legacyMoves = new HashSet<>();
    private Set<String> tags = new HashSet<>();
    private Boolean tradable;
    private Integer levelFloor;
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

    public boolean isTradable() {
        return tradable != null ? tradable : !tags.contains("mythical");
    }

    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public int getLevelFloor() {
        if (levelFloor != null) return levelFloor;
        if (tags.contains("legendary") || tags.contains("mythical")) return 15;
        return 1;
    }

    public void setLevelFloor(int levelFloor) {
        this.levelFloor = levelFloor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return dex == pokemon.dex;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
