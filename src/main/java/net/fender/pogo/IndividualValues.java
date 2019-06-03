package net.fender.pogo;

import java.math.BigDecimal;
import java.util.Objects;

public class IndividualValues {

    public static final IndividualValues ZERO = new IndividualValues(0, 0, 0);
    public static final IndividualValues PERFECT = new IndividualValues(15, 15, 15);

    private final BigDecimal attack;
    private final BigDecimal defense;
    private final BigDecimal stamina;

    public IndividualValues(int attack, int defense, int stamina) {
        this.attack = BigDecimal.valueOf(attack);
        this.defense = BigDecimal.valueOf(defense);
        this.stamina = BigDecimal.valueOf(stamina);
    }

    public BigDecimal getAttack() {
        return attack;
    }

    public BigDecimal getDefense() {
        return defense;
    }

    public BigDecimal getStamina() {
        return stamina;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualValues that = (IndividualValues) o;
        return attack.intValue() == that.attack.intValue() &&
                defense.intValue() == that.defense.intValue() &&
                stamina.intValue() == that.stamina.intValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack.intValue(), defense.intValue(), stamina.intValue());
    }
}
