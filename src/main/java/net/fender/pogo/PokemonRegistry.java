package net.fender.pogo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.SimilarityScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class PokemonRegistry {

    private static final SimilarityScore<Double> SIMILARITY_SCORE = new JaroWinklerSimilarity();
    private final Map<String, Pokemon> pokemonByName = new HashMap<>();

    @Autowired
    public PokemonRegistry(ObjectMapper mapper, ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:base_stats.json");
        ArrayNode root = (ArrayNode) mapper.readTree(resource.getInputStream());
        for (int i = 0; i < root.size(); i++) {
            JsonNode pokemonNode = root.get(i);
            String name = pokemonNode.get("pokemon_name").textValue().toLowerCase();
            if (pokemonNode.hasNonNull("form")) {
                String form = pokemonNode.get("form").textValue().toLowerCase();
                if (!"normal".equals(form)) {
                    name = name + "-" + form;
                }
            }
            int attack = pokemonNode.get("base_attack").intValue();
            int defense = pokemonNode.get("base_defense").intValue();
            int stamina = pokemonNode.get("base_stamina").intValue();
            boolean tradable = pokemonNode.hasNonNull("tradable") ? pokemonNode.get("tradable").booleanValue() : true;
            int levelFloor = pokemonNode.hasNonNull("level_floor") ? pokemonNode.get("level_floor").intValue() : 1;
            Pokemon pokemon = new Pokemon(name, attack, defense, stamina, tradable, levelFloor);
            pokemonByName.put(name, pokemon);
        }
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
            String pokemonName = pokemon.getName();
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
