package net.fender.pogo;

import java.util.Objects;

public class IndividualValues {

    public static final IndividualValues ZERO = new IndividualValues(0, 0, 0);
    public static final IndividualValues PERFECT = new IndividualValues(15, 15, 15);

    private final int attack;
    private final int defense;
    private final int stamina;

    public IndividualValues(int attack, int defense, int stamina) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualValues that = (IndividualValues) o;
        return attack == that.attack &&
                defense == that.defense &&
                stamina == that.stamina;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack, defense, stamina);
    }

    @Override
    public String toString() {
        return attack + "/" + defense + "/" + stamina;
    }
}
