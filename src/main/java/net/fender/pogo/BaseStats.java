package net.fender.pogo;

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
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
