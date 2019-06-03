package net.fender.pogo;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class BaseStats {

    private final int attack;
    private final int defense;
    private final int stamina;

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

        double speedModifier = 1 + (speed - 75) / 500.0;

        int minAttack = Math.min(physicalAttack, specialAttack);
        int maxAttack = Math.max(physicalAttack, specialAttack);
        double scaledMaxAttack = 7.0 / 8.0 * maxAttack;
        double scaledMinAttack = 1.0 / 8.0 * minAttack;
        int scaledAttack = (int) Math.round(2.0 * (scaledMaxAttack + scaledMinAttack));
        attack = (int) Math.round(scaledAttack * speedModifier);

        int minDefense = Math.min(physicalDefense, specialDefense);
        int maxDefense = Math.max(physicalDefense, specialDefense);
        double scaledMaxDefense = 5.0 / 8.0 * maxDefense;
        double scaledMinDefense = 3.0 / 8.0 * minDefense;
        int scaledDefense = (int) Math.round(2.0 * (scaledMaxDefense + scaledMinDefense));
        defense = (int) Math.round(scaledDefense * speedModifier);

        stamina = (int) Math.floor(msgHp * 1.75 + 50);
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
