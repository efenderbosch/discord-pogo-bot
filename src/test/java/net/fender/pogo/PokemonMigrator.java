package net.fender.pogo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sargunvohra.lib.pokekotlin.client.ErrorResponse;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonStat;
import net.fender.pvpoke.BaseStats;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public class PokemonMigrator {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonMigrator.class);

    @Test
    public void migrate() throws JsonProcessingException {
        PokeApiClient client = new PokeApiClient();
        List<net.fender.pvpoke.Pokemon> pokemon = new ArrayList<>(807 - 650);
        for (int dex = 650; dex <= 807; dex++) {
            net.fender.pvpoke.Pokemon migrated = migrate(client, dex);
            pokemon.add(migrated);
        }
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(NON_EMPTY);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pokemon);
        System.out.println(json);
    }

    private net.fender.pvpoke.Pokemon migrate(PokeApiClient client, int dex) {
        Pokemon pokemon = null;
        try {
            pokemon = client.getPokemon(dex);
        } catch (Throwable t) {
            if (t instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) t;
                if (errorResponse.getCode() == 404) {
                    LOG.warn("{} not found", dex);
                }
            }
            return null;
        }

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
        int attack = (int) Math.round(scaledAttack * speedModifier);

        int minDefense = Math.min(physicalDefense, specialDefense);
        int maxDefense = Math.max(physicalDefense, specialDefense);
        double scaledMaxDefense = 5.0 / 8.0 * maxDefense;
        double scaledMinDefense = 3.0 / 8.0 * minDefense;
        int scaledDefense = (int) Math.round(2.0 * (scaledMaxDefense + scaledMinDefense));
        int defense = (int) Math.round(scaledDefense * speedModifier);

        int stamina = (int) Math.floor(msgHp * 1.75 + 50);
        net.fender.pvpoke.Pokemon converted = new net.fender.pvpoke.Pokemon();
        converted.setDex(pokemon.getId());
        converted.setSpeciesId(pokemon.getName());
        BaseStats baseStats = new BaseStats(attack, defense, stamina);
        converted.setBaseStats(baseStats);
        return converted;
    }
}
