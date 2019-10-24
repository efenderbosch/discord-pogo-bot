package net.fender.pvpoke;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BaseStats {

    private final Integer attack;
    private final Integer defense;
    private final Integer stamina;

    @JsonCreator
    public BaseStats(@Nonnull @JsonProperty(value = "atk", required = true) Integer attack,
                     @Nonnull @JsonProperty(value = "def", required = true) Integer defense,
                     @Nonnull @JsonProperty("hp") Integer stamina) {
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
    }

    @JsonProperty("atk")
    public Integer getAttack() {
        return attack;
    }

    @JsonProperty("def")
    public Integer getDefense() {
        return defense;
    }

    @JsonProperty("hp")
    public Integer getStamina() {
        return stamina;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseStats baseStats = (BaseStats) o;
        return Objects.equals(attack, baseStats.attack) &&
                Objects.equals(defense, baseStats.defense) &&
                Objects.equals(stamina, baseStats.stamina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack, defense, stamina);
    }
}
