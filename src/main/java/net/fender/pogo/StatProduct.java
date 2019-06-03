package net.fender.pogo;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_UP;

public class StatProduct implements Comparable<StatProduct> {

    private final double level;
    private final BaseStats stats;
    private final BigDecimal levelAttack;
    private final BigDecimal levelDefense;
    private final BigDecimal hp;
    private int statProduct;
    private final int cp;
    private final IndividualValues ivs;

    public StatProduct(BaseStats stats, IndividualValues ivs, double level) {
        this.level = level;
        this.stats = stats;
        this.ivs = ivs;
        BigDecimal cpModifier = CpModifier.getCpModifier(level);
        levelAttack = cpModifier.multiply(stats.getAttack().add(ivs.getAttack()));
        levelDefense = cpModifier.multiply(stats.getDefense().add(ivs.getDefense()));
        hp = cpModifier.multiply(stats.getStamina().add(ivs.getStamina())).setScale(0, HALF_UP);
        statProduct = levelAttack.multiply(levelDefense).multiply(hp).setScale(0, HALF_UP).intValue();
        cp = stats.getAttack().add(ivs.getAttack()).
                multiply(stats.getDefense().add(ivs.getDefense()).sqrt(DECIMAL64)).
                multiply(stats.getStamina().add(ivs.getStamina()).sqrt(DECIMAL64)).
                multiply(cpModifier.pow(2)).
                divide(BigDecimal.TEN).
                setScale(0, HALF_UP).
                intValue();
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

    public BigDecimal getLevelAttack() {
        return levelAttack;
    }

    public BigDecimal getLevelDefense() {
        return levelDefense;
    }

    public BigDecimal getHp() {
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
        return ivs.getAttack().intValue() >= floor &&
                ivs.getDefense().intValue() >= floor &&
                ivs.getStamina().intValue() >= floor;
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
        // TODO scale BigDecimal to 2
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("level", level);
        builder.append("atk-iv", ivs.getAttack());
        builder.append("def-iv", ivs.getDefense());
        builder.append("sta-iv", ivs.getStamina());
        builder.append("atk", levelAttack.setScale(2, HALF_UP));
        builder.append("def", levelDefense.setScale(2, HALF_UP));
        builder.append("hp", hp.intValue());
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
