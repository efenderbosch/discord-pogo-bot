package net.fender.pogo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_UP;

public class StatProduct {

    private final double level;
    private final BaseStats stats;
    private final BigDecimal levelAttack;
    private final BigDecimal levelDefense;
    private final BigDecimal hp;
    private int statProduct;
    private final int cp;

    public StatProduct(BaseStats stats, IndividualValues ivs, double level) {
        this.level = level;
        this.stats = stats;
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

    @Override
    public String toString() {
        // TODO scale BigDecimal to 2
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("level", level);
        //builder.append("base atk", stats.getAttack().setScale(0, HALF_UP));
        //builder.append("base def", stats.getDefense().setScale(0, HALF_UP));
        //builder.append("base stamina", stats.getStamina().setScale(0, HALF_UP).intValue());
        builder.append("atk", levelAttack.setScale(2, HALF_UP));
        builder.append("def", levelDefense.setScale(2, HALF_UP));
        builder.append("hp", hp.intValue());
        builder.append("product", statProduct);
        builder.append("cp", cp);
        return builder.toString();
    }
}
