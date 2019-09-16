package net.fender.pogo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pvpoke.GameMaster;
import net.fender.pvpoke.Pokemon;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.SimilarityScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class PokemonRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonRegistry.class);

    private static final SimilarityScore<Double> SIMILARITY_SCORE = new JaroWinklerSimilarity();
    private final Map<String, Pokemon> pokemonByName = new HashMap<>();

    @Autowired
    public PokemonRegistry(ObjectMapper mapper, ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:gamemaster.json");
        LOG.info("loading gamemaster.json");
        GameMaster gameMaster = mapper.readValue(resource.getInputStream(), GameMaster.class);
        for (Pokemon pokemon : gameMaster.getPokemon()) {
            pokemon.setSpeciesId(pokemon.getSpeciesId().replace('_', '-'));
            pokemonByName.put(pokemon.getSpeciesId(), pokemon);
        }
        resource = resourceLoader.getResource("classpath:custom.json");
        LOG.info("loading custom.json");
        gameMaster = mapper.readValue(resource.getInputStream(), GameMaster.class);
        for (Pokemon pokemon : gameMaster.getPokemon()) {
            pokemonByName.put(pokemon.getSpeciesId(), pokemon);
        }
        LOG.info("finished");
    }

    public Pokemon getPokeman(String name) {
        return pokemonByName.get(name.replace("alolan", "alola"));
    }

    public Collection<Pokemon> getAll() {
        return pokemonByName.values();
    }

    public List<String> find(String search) {
        NavigableMap<Double, List<String>> scores = new TreeMap<>(Comparator.reverseOrder());
        for (Pokemon pokemon : pokemonByName.values()) {
            String pokemonName = pokemon.getSpeciesId();
            Double score = SIMILARITY_SCORE.apply(pokemonName, search);
            List<String> withScore = scores.get(score);
            if (withScore == null) {
                withScore = new ArrayList<>();
                scores.put(score, withScore);
            }
            withScore.add(pokemonName);
        }
        return scores.headMap(0.85).values().stream().flatMap(Collection::stream).limit(9).collect(toList());
    }
}
