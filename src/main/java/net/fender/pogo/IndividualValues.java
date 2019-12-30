package net.fender.pogo;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndividualValues {

    public static final IndividualValues ZERO = new IndividualValues(0, 0, 0);
    public static final IndividualValues PERFECT = new IndividualValues(15, 15, 15);
    private static final Pattern PATTERN = Pattern.compile("(?<atk>\\d{1,2})[\\/|\\s]+(?<def>\\d{1,2})[\\/|\\s]+(?<sta>\\d{1,2})");

    private final int attack;
    private final int defense;
    private final int stamina;

    public IndividualValues(int attack, int defense, int stamina) {
        this.attack = attack;
        this.defense = defense;
        this.stamina = stamina;
    }

    public static IndividualValues parse(String ivs) {
        Matcher matcher = PATTERN.matcher(ivs);
        if (!matcher.matches()) return ZERO;
        return new IndividualValues(Integer.parseInt(matcher.group("atk")),
                Integer.parseInt(matcher.group("def")),
                Integer.parseInt(matcher.group("sta")));
    }

    public static IndividualValues floorNonTradable(IndividualValues other) {
        return new IndividualValues(
                Math.max(other.getAttack(), 10),
                Math.max(other.getDefense(), 10),
                Math.max(other.getStamina(), 10));
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
