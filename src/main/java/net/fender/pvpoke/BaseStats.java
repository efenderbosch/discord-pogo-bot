package net.fender.pvpoke;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class BaseStats {

    private final int attack;
    private final int defense;
    private final int stamina;

    @JsonCreator
    public BaseStats(@JsonProperty("atk") int attack,
                     @JsonProperty("def") int defense,
                     @JsonProperty("hp") int stamina) {
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
    }

    @JsonProperty("atk")
    public int getAttack() {
        return attack;
    }

    @JsonProperty("def")
    public int getDefense() {
        return defense;
    }

    @JsonProperty("hp")
    public int getStamina() {
        return stamina;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
