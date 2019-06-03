package net.fender.pogo;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StatProduct implements Comparable<StatProduct> {

    private final double level;
    private final BaseStats stats;
    private final double levelAttack;
    private final double levelDefense;
    private final int hp;
    private int statProduct;
    private final int cp;
    private final IndividualValues ivs;

    public StatProduct(BaseStats stats, IndividualValues ivs, double level) {
        this.level = level;
        this.stats = stats;
        this.ivs = ivs;

        double cpModifier = CpModifier.getCpModifier(level);

        int attack = stats.getAttack() + ivs.getAttack();
        int defense = stats.getDefense() + ivs.getDefense();
        int stamina = stats.getStamina() + ivs.getStamina();

        levelAttack = cpModifier * attack;
        levelDefense = cpModifier * defense;
        hp = (int) Math.floor(cpModifier * stamina);

        statProduct = (int) Math.round(levelAttack * levelDefense * hp);

        double d = Math.sqrt(defense);
        double s = Math.sqrt(stamina);
        cp = (int) Math.floor(attack * d * s * cpModifier * cpModifier / 10.0);
    }

    public double getLevel() {
        return level;
    }

    public BaseStats getStats() {
        return stats;
    }

    public int getStatProduct() {
        return statProduct;
    }

    public int getCp() {
        return cp;
    }

    public double getLevelAttack() {
        return levelAttack;
    }

    public double getLevelDefense() {
        return levelDefense;
    }

    public int getHp() {
        return hp;
    }

    public IndividualValues getIvs() {
        return ivs;
    }

    public boolean isWild() {
        return testIVs(0);
    }

    public boolean isGoodFriend() {
        return testIVs(1);
    }

    public boolean isGreatFriend() {
        return testIVs(2);
    }

    public boolean isUltraFriend() {
        return testIVs(3);
    }

    public boolean isWeatherBoosted() {
        return testIVs(4);
    }

    public boolean isBestFriend() {
        return testIVs(5);
    }

    public boolean isRaidHatchResearch() {
        return testIVs(10);
    }

    public boolean isLucky() {
        return testIVs(12);
    }

    private boolean testIVs(int floor) {
        return ivs.getAttack() >= floor &&
                ivs.getDefense() >= floor &&
                ivs.getStamina() >= floor;
    }

    public static StatProduct generateStatProduct(Pokemon pokemon, IndividualValues ivs, League league) {
        BaseStats baseStats = new BaseStats(pokemon);
        StatProduct bestStatProduct = null;
        for (double level = 1.0; level <= 40.0; level += 0.5) {
            StatProduct statProduct = new StatProduct(baseStats, ivs, level);
            int cp = statProduct.getCp();
            if (cp <= league.maxCp) {
                bestStatProduct = statProduct;
            }
        }
        return bestStatProduct;
    }

    public static Map<IndividualValues, StatProduct> generateStatProducts(Pokemon pokemon, League league) {
        BaseStats baseStats = new BaseStats(pokemon);
        StatProduct zero = generateStatProduct(pokemon, IndividualValues.ZERO, league);
        StatProduct perfect = generateStatProduct(pokemon, IndividualValues.PERFECT, league);

        Map<IndividualValues, StatProduct> stats = new HashMap<>(4096);
        for (int atk = 0; atk <= 15; atk++) {
            for (int def = 0; def <= 15; def++) {
                for (int sta = 0; sta <= 15; sta++) {
                    IndividualValues ivs = new IndividualValues(atk, def, sta);
                    for (double level = perfect.level; level <= zero.level; level += 0.5) {
                        StatProduct statProduct = new StatProduct(baseStats, ivs, level);
                        int cp = statProduct.getCp();
                        if (cp <= league.maxCp) {
                            stats.put(ivs, statProduct);
                        }
                    }
                }
            }
        }
        return stats;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("level", level);
        builder.append("atk-iv", ivs.getAttack());
        builder.append("def-iv", ivs.getDefense());
        builder.append("sta-iv", ivs.getStamina());
        builder.append("atk", levelAttack);
        builder.append("def", levelDefense);
        builder.append("hp", hp);
        builder.append("product", statProduct);
        builder.append("cp", cp);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return ivs.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatProduct that = (StatProduct) o;
        return ivs.equals(that.ivs);
    }

    @Override
    public int compareTo(@NotNull StatProduct o) {
        if (statProduct != o.statProduct) {
            return o.statProduct - statProduct;
        }
        return o.cp - cp;
    }
}
