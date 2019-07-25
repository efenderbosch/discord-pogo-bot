package net.fender.pvpoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMaster {

    private Settings settings;
    private Map<String, List<String>> cups = new HashMap<>();
    private List<Pokemon> pokemon = new ArrayList<>();
    private List<Move> moves = new ArrayList<>();

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Map<String, List<String>> getCups() {
        return cups;
    }

    public void setCups(Map<String, List<String>> cups) {
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
}
