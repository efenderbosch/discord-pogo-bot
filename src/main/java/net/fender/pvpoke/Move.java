package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;

public class Move {

    private String moveId;
    private String name;
    private String type;
    private int power;
    private int energy;
    private int energyGain;
    private int cooldown;
    private int[] buffs = new int[0];
    private BuffTarget buffTarget;
    private double buffApplyChance;

    public String getMoveId() {
        return moveId;
    }

    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyGain() {
        return energyGain;
    }

    public void setEnergyGain(int energyGain) {
        this.energyGain = energyGain;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int[] getBuffs() {
        return buffs;
    }

    public void setBuffs(int[] buffs) {
        this.buffs = buffs;
    }

    public BuffTarget getBuffTarget() {
        return buffTarget;
    }

    public void setBuffTarget(BuffTarget buffTarget) {
        this.buffTarget = buffTarget;
    }

    public double getBuffApplyChance() {
        return buffApplyChance;
    }

    public void setBuffApplyChance(double buffApplyChance) {
        this.buffApplyChance = buffApplyChance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(moveId, move.moveId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveId);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public enum BuffTarget {
        self, opponent
    }

}
