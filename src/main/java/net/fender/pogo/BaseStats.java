package net.fender.pogo;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

public class BaseStats {

    private static final BigDecimal FIVE_HUNDRED = new BigDecimal("500.00");
    private static final BigDecimal SPEED_SCALE = new BigDecimal("1.75");
    private static final BigDecimal SEVEN_EIGHTHS = BigDecimal.valueOf(7).divide(BigDecimal.valueOf(8));
    private static final BigDecimal ONE_EIGHTH = BigDecimal.ONE.divide(BigDecimal.valueOf(8));
    private static final BigDecimal FIVE_EIGHTS = BigDecimal.valueOf(5).divide(BigDecimal.valueOf(8));
    private static final BigDecimal THREE_EIGHTS = BigDecimal.valueOf(3).divide(BigDecimal.valueOf(8));

    private final BigDecimal attack;
    private final BigDecimal defense;
    private final BigDecimal stamina;

    public BaseStats(Pokemon pokemon) {
        List<PokemonStat> stats = pokemon.getStats();
        int physicalAttack = 100;
        int physicalDefense = 100;
        int speed = 75;
        int msgHp = 100;
        int specialAttack = 100;
        int specialDefense = 100;
        for (PokemonStat stat : stats) {
            switch (stat.getStat().getName()) {
                case "attack":
                    physicalAttack = stat.getBaseStat();
                    break;
                case "defense":
                    physicalDefense = stat.getBaseStat();
                    break;
                case "speed":
                    speed = stat.getBaseStat();
                    break;
                case "hp":
                    msgHp = stat.getBaseStat();
                    break;
                case "special-attack":
                    specialAttack = stat.getBaseStat();
                    break;
                case "special-defense":
                    specialDefense = stat.getBaseStat();
                    break;
            }
        }

        BigDecimal speedModifier = BigDecimal.valueOf(speed - 75).
                divide(FIVE_HUNDRED).
                add(BigDecimal.ONE);

        BigDecimal minAttack = BigDecimal.valueOf(Math.min(physicalAttack, specialAttack));
        BigDecimal maxAttack = BigDecimal.valueOf(Math.max(physicalAttack, specialAttack));
        attack = SEVEN_EIGHTHS.multiply(maxAttack).
                add(ONE_EIGHTH.multiply(minAttack)).
                multiply(BigDecimal.valueOf(2)).
                multiply(speedModifier).
                setScale(0, HALF_UP);

        BigDecimal minDefense = BigDecimal.valueOf(Math.min(physicalDefense, specialDefense));
        BigDecimal maxDefense = BigDecimal.valueOf(Math.max(physicalDefense, specialDefense));
        defense = FIVE_EIGHTS.multiply(maxDefense).
                add(THREE_EIGHTS.multiply(minDefense)).
                multiply(BigDecimal.valueOf(2)).multiply(speedModifier).
                setScale(0, HALF_UP);

        stamina = SPEED_SCALE.
                multiply(BigDecimal.valueOf(msgHp)).
                add(BigDecimal.valueOf(50)).
                setScale(0, HALF_UP);
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
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
