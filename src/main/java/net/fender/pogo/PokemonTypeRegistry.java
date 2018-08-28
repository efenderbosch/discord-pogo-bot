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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@Component
public class PokemonTypeRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonTypeRegistry.class);

    private final String path;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper mapper;
    private final Map<String, PokemonType> typesByName = new HashMap<>();

    @Autowired
    public PokemonTypeRegistry(ResourceLoader resourceLoader, ObjectMapper mapper) {
        path = CLASSPATH_URL_PREFIX + "/types.json";
        this.resourceLoader = resourceLoader;
        this.mapper = mapper;
    }

    @PostConstruct
    public void load() throws IOException {
        Resource resource = resourceLoader.getResource(path);
        InputStream inputStream = resource.getInputStream();
        ArrayNode nodes = (ArrayNode) mapper.readTree(inputStream);
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            ObjectNode typeNode = (ObjectNode) nodes.get(i);
            String name = typeNode.get("name").asText();
            PokemonType pokemonType = new PokemonType(name);
            typesByName.put(name, pokemonType);
        }

        for (int i = 0; i < size; i++) {
            ObjectNode typeNode = (ObjectNode) nodes.get(i);
            String name = typeNode.get("name").asText();
            LOG.info("loading {}", name);
            PokemonType pokemonType = typesByName.get(name);

            ArrayNode strongNodes = (ArrayNode) typeNode.get("strong_vs");
            int strongSize = strongNodes.size();
            if (strongSize == 0) {
                LOG.warn("no strong_vs found for {}", name);
                continue;
            }

            for (int j = 0; j < strongSize; j++) {
                JsonNode strongNode = strongNodes.get(j);
                String strongName = strongNode.asText();
                PokemonType strongType = typesByName.get(strongName);
                pokemonType.addStrongVs(strongType);
            }

            ArrayNode weakNodes = (ArrayNode) typeNode.get("weak_vs");
            int weakSize = weakNodes.size();
            if (weakSize == 0) {
                LOG.warn("no weak_vs found for {}", name);
                continue;
            }

            for (int j = 0; j < weakSize; j++) {
                JsonNode weakNode = weakNodes.get(j);
                String weakName = weakNode.asText();
                PokemonType weakType = typesByName.get(weakName);
                pokemonType.addWeakVs(weakType);
            }
            LOG.info("loaded {}", pokemonType);
        }
    }

    public PokemonType getPokemonTypeByName(String name) {
        return typesByName.get(name);
    }
}
