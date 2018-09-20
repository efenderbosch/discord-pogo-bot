package net.fender.pogo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

//@Component
public class PokemonRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonRegistry.class);

    private final String path;
    private final ResourceLoader resourceLoader;
    private final PokemonTypeRegistry typeRegistry;
    private final ObjectMapper mapper;
    private final Map<Integer, Pokemon> pokemonByDex = new HashMap<>();
    private final Map<String, Pokemon> pokemonByName = new HashMap<>();

    @Autowired
    public PokemonRegistry(ResourceLoader resourceLoader,
                           PokemonTypeRegistry typeRegistry,
                           ObjectMapper mapper) {
        path = CLASSPATH_URL_PREFIX + "/pokemon.json";
        this.resourceLoader = resourceLoader;
        this.typeRegistry = typeRegistry;
        this.mapper = mapper;
    }

    @PostConstruct
    public void load() throws IOException {
        Resource resource = resourceLoader.getResource(path);
        InputStream inputStream = resource.getInputStream();
        ArrayNode nodes = (ArrayNode) mapper.readTree(inputStream);
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            ObjectNode pokemonNode = (ObjectNode) nodes.get(i);
            int dex = pokemonNode.get("dex").asInt();
            String name = pokemonNode.get("name").asText();
            String primaryTypeName = pokemonNode.get("primary_type").asText();
            PokemonType primaryType = typeRegistry.getPokemonTypeByName(primaryTypeName);

            PokemonType secondaryType = null;
            JsonNode secondaryTypeNode = pokemonNode.get("secondary_type");
            if (secondaryTypeNode != null) {
                String secondaryTypeName = secondaryTypeNode.asText();
                secondaryType = typeRegistry.getPokemonTypeByName(secondaryTypeName);
            }

            Pokemon pokemon = new Pokemon(dex, name, primaryType, secondaryType);
            pokemonByDex.put(dex, pokemon);
            pokemonByName.put(name, pokemon);

            JsonNode raidLevelNode = pokemonNode.get("raid_level");
            if (raidLevelNode != null) {
                RaidLevel raidLevel = RaidLevel.RAIDS_BY_LEVEL.get(raidLevelNode.asText());
                pokemon.setRaidLevel(raidLevel);
            }

            JsonNode attackNode = pokemonNode.get("attack");
            if (attackNode != null) {
                pokemon.setAttack(attackNode.intValue());
            }

            JsonNode defenseNode = pokemonNode.get("defense");
            if (defenseNode != null) {
                pokemon.setDefense(defenseNode.intValue());
            }

            JsonNode staminaNode = pokemonNode.get("stamina");
            if (staminaNode != null) {
                pokemon.setStamina(staminaNode.asInt());
            }

            LOG.info("loaded pokemon {}", pokemon);
        }
    }

    public Pokemon getPokemonByDex(int dex) {
        return pokemonByDex.get(dex);
    }

    public Optional<Pokemon> getPokemonByName(String name) {
        return Optional.ofNullable(pokemonByName.get(name));
    }
}
