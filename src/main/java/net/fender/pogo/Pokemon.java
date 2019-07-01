package net.fender.pogo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;

public class Pokemon {

    private final String name;
    private final int attack;
    private final int defense;
    private final int stamina;
    private final boolean tradable;
    private final int levelFloor;

    public Pokemon(String name, int attack, int defense, int stamina, boolean tradable, int levelFloor) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
        this.tradable = tradable;
        this.levelFloor = levelFloor;
    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getStamina() {
        return stamina;
    }

    public boolean isTradable() {
        return tradable;
    }

    public int getLevelFloor() {
        return levelFloor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokemon pokemon = (Pokemon) o;
        return Objects.equals(name, pokemon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
