package net.fender.pogo;

import net.fender.pvpoke.BaseStats;
import net.fender.pvpoke.Pokemon;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class StatProduct implements Comparable<StatProduct> {

    private final double level;
    private final double levelAttack;
    private final double levelDefense;
    private final int hp;
    private double statProduct;
    private final int cp;
    private final IndividualValues ivs;

    public StatProduct(Pokemon pokemon, IndividualValues ivs, double level) {
        this(pokemon.getBaseStats(), ivs, level);
    }

    public StatProduct(BaseStats baseStats, IndividualValues ivs, double level) {
        this.level = level;
        this.ivs = ivs;

        double cpModifier = CpModifier.getCpModifier(level);

        int attack = baseStats.getAttack() + ivs.getAttack();
        int defense = baseStats.getDefense() + ivs.getDefense();
        int stamina = baseStats.getStamina() + ivs.getStamina();

        levelAttack = cpModifier * attack;
        levelDefense = cpModifier * defense;
        hp = (int) Math.floor(cpModifier * stamina);

        statProduct = levelAttack * levelDefense * hp;

        double d = Math.sqrt(defense);
        double s = Math.sqrt(stamina);
        cp = Math.max(10, (int) Math.floor(attack * d * s * cpModifier * cpModifier / 10.0));
    }

    public double getLevel() {
        return level;
    }

    public double getStatProduct() {
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

    public boolean isTradeLevel(TradeLevel tradeLevel) {
        return TradeLevel.getTradeLevel(ivs).getFloor() >= tradeLevel.getFloor();
    }

    public static LinkedHashMap<IndividualValues, StatProduct> generateStatProducts(Pokemon pokemon,
                                                                                    League league,
                                                                                    int maxLevel) {
        double startLevel = pokemon.getLevelFloor();

        int minIv = pokemon.isTradable() ? 0 : 10;
        int size = (16 - minIv) * (16 - minIv) * (16 - minIv);
        Map<IndividualValues, StatProduct> stats = new HashMap<>(size, 1.0f);
        for (int atk = minIv; atk <= 15; atk++) {
            for (int def = minIv; def <= 15; def++) {
                for (int sta = minIv; sta <= 15; sta++) {
                    IndividualValues ivs = new IndividualValues(atk, def, sta);
                    for (double level = startLevel; level <= maxLevel; level += 0.5) {
                        StatProduct statProduct = new StatProduct(pokemon, ivs, level);
                        int cp = statProduct.getCp();
                        if (cp <= league.maxCp) {
                            stats.put(ivs, statProduct);
                        }
                    }
                }
            }
        }
        // sort by StatProduct
        return stats.entrySet().stream().
                sorted(comparingByValue()).
                collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("level", level);
        builder.append("IVs", ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina());
        builder.append("atk", round(levelAttack));
        builder.append("def", round(levelDefense));
        builder.append("hp", hp);
        builder.append("product", statProduct);
        builder.append("cp", cp);
        return builder.toString();
    }

    public static String round(double d) {
        return "" + (Math.round(d * 10.0) / 10.0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cp, ivs, level);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatProduct other = (StatProduct) o;
        return cp == other.cp && Objects.equals(ivs, other.ivs) && level == other.level;
    }

    @Override
    public int compareTo(@Nonnull StatProduct o) {
        if (statProduct != o.statProduct) {
            return Double.compare(o.statProduct, statProduct);
        }
        return o.cp - cp;
    }
}
