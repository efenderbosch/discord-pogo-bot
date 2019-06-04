package net.fender.pogo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;

public class Pokemon {

    private final String name;
    private final int attack;
    private final int defense;
    private final int stamina;

    public Pokemon(String name, int attack, int defense, int stamina) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
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
