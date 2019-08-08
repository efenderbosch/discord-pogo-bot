package net.fender.pogo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class StatProduct implements Comparable<StatProduct> {

    private final double level;
    private final double levelAttack;
    private final double levelDefense;
    private final int hp;
    private int statProduct;
    private final int cp;
    private final IndividualValues ivs;

    public StatProduct(Pokemon pokemon, IndividualValues ivs, double level) {
        this.level = level;
        this.ivs = ivs;

        double cpModifier = CpModifier.getCpModifier(level);

        int attack = pokemon.getAttack() + ivs.getAttack();
        int defense = pokemon.getDefense() + ivs.getDefense();
        int stamina = pokemon.getStamina() + ivs.getStamina();

        levelAttack = cpModifier * attack;
        levelDefense = cpModifier * defense;
        hp = (int) Math.floor(cpModifier * stamina);

        statProduct = (int) Math.round(levelAttack * levelDefense * hp);

        double d = Math.sqrt(defense);
        double s = Math.sqrt(stamina);
        cp = Math.max(10, (int) Math.floor(attack * d * s * cpModifier * cpModifier / 10.0));
    }

    public double getLevel() {
        return level;
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

    public TradeLevel getTradeLevel() {
        return TradeLevel.getTradeLevel(ivs);
    }

    public boolean isAttackBest() {
        return ivs.getAttack() >= ivs.getDefense() && ivs.getAttack() >= ivs.getStamina();
    }

    public boolean isNotGreatInBattle() {
        return ivs.getAttack() + ivs.getDefense() + ivs.getStamina() <= 22;
    }

    public boolean isDecent() {
        int total = ivs.getAttack() + ivs.getDefense() + ivs.getStamina();
        return total > 22 && total <= 29;
    }

    public boolean isStrong() {
        int total = ivs.getAttack() + ivs.getDefense() + ivs.getStamina();
        return total > 29 && total <= 36;
    }

    public boolean isAmazes() {
        return ivs.getAttack() + ivs.getDefense() + ivs.getStamina() > 36;
    }

    public static Map<IndividualValues, StatProduct> generateStatProducts(Pokemon pokemon, League league) {
        double startLevel = pokemon.getLevelFloor();

        int minIv = pokemon.isTradable() ? 0 : 10;
        Map<IndividualValues, StatProduct> stats = new HashMap<>(4096);
        for (int atk = minIv; atk <= 15; atk++) {
            for (int def = minIv; def <= 15; def++) {
                for (int sta = minIv; sta <= 15; sta++) {
                    IndividualValues ivs = new IndividualValues(atk, def, sta);
                    for (double level = startLevel; level <= 40.0; level += 0.5) {
                        StatProduct statProduct = new StatProduct(pokemon, ivs, level);
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
        builder.append("IVs", ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina());
        builder.append("atk", round(levelAttack));
        builder.append("def", round(levelDefense));
        builder.append("hp", hp);
        builder.append("product", statProduct);
        builder.append("cp", cp);
        return builder.toString();
    }

    public static String round(double d) {
        return "" + (Math.round(d * 100.0) / 100.0);
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
    public int compareTo(@Nonnull StatProduct o) {
        if (statProduct != o.statProduct) {
            return o.statProduct - statProduct;
        }
        return o.cp - cp;
    }
}
