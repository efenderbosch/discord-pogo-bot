package net.fender.pogo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class Pokemon {

    private final int dex;
    private final String name;
    private final PokemonType primaryType;
    private final PokemonType secondaryType;
    private RaidLevel raidLevel = RaidLevel.UNKNOWN;
    private int attack;
    private int defense;
    private int stamina;

    public Pokemon(int dex, String name, PokemonType primaryType, PokemonType secondaryType) {
        this.dex = dex;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }

    public int getDex() {
        return dex;
    }

    public String getName() {
        return name;
    }

    public PokemonType getPrimaryType() {
        return primaryType;
    }

    public PokemonType getSecondaryType() {
        return secondaryType;
    }

    public RaidLevel getRaidLevel() {
        return raidLevel;
    }

    public void setRaidLevel(RaidLevel raidLevel) {
        this.raidLevel = raidLevel;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
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
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("dex", dex);
        builder.append("name", name);
        builder.append("primaryType", primaryType.getName());
        builder.append("secondaryType", secondaryType == null ? null : secondaryType.getName());
        builder.append("base stats", attack + "/" + defense + "/" + stamina);
        return builder.build();
    }
}
