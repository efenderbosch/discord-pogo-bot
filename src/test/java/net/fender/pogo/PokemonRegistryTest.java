package net.fender.pogo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class PokemonRegistryTest {

    @Test
    public void test_find() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        System.out.println(pokemonRegistry.getPokeman("raticate"));
        List<String> found = pokemonRegistry.find("raticate-alolan");
        System.out.print(found.stream().collect(joining(", ")));
    }
}
