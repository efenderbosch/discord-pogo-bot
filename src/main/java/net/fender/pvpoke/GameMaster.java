package net.fender.pvpoke;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

public class GameMaster {

    private final Settings settings;
    private final List<Cup> cups;
    private final List<Pokemon> pokemon;
    private final List<Move> moves;

    @JsonCreator
    public GameMaster(@JsonProperty("settings") Settings settings,
                      @JsonProperty("cups") List<Cup> cups,
                      @JsonProperty("pokemon") List<Pokemon> pokemon,
                      @JsonProperty("moves") List<Move> moves) {
        this.settings = settings;
        this.cups = cups;
        this.pokemon = pokemon;
        this.moves = moves;
    }

    public Settings getSettings() {
        return settings;
    }

    public List<Cup> getCups() {
        return cups;
    }

    public List<Pokemon> getPokemon() {
        return pokemon;
    }

    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
