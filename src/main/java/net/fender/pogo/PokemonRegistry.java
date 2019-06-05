package net.fender.pogo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PokemonRegistry {

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
            Pokemon pokemon = new Pokemon(name, attack, defense, stamina, tradable);
            pokemonByName.put(name, pokemon);
        }
    }

    public Pokemon getPokeman(String name) {
        return pokemonByName.get(name);
    }
}
