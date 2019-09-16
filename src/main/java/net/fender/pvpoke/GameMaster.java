package net.fender.pvpoke;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class GameMaster {

    private Settings settings;
    private List<Cup> cups = new ArrayList<>();
    private List<Pokemon> pokemon = new ArrayList<>();
    private List<Move> moves = new ArrayList<>();

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<Cup> getCups() {
        return cups;
    }

    public void setCups(List<Cup> cups) {
        this.cups = cups;
    }

    public List<Pokemon> getPokemon() {
        return pokemon;
    }

    public void setPokemon(List<Pokemon> pokemon) {
        this.pokemon = pokemon;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
