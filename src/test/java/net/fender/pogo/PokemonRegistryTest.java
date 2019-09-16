package net.fender.pogo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pvpoke.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PokemonRegistryTest {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
    private final PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);

    public PokemonRegistryTest() throws IOException { }

    @Test
    public void test_find() throws IOException {
        System.out.println(pokemonRegistry.getPokeman("raticate"));
        List<String> found = pokemonRegistry.find("raticate-alolan");
        System.out.print(found.stream().collect(joining(", ")));
    }

    @Test
    public void test_mythical_tradable() {
        Pokemon mew = pokemonRegistry.getPokeman("mew");
        assertThat(mew.isTradable(), is(false));
    }

    @Test
    public void test_melmetal_tradable() {
        Pokemon mew = pokemonRegistry.getPokeman("melmetal");
        assertThat(mew.isTradable(), is(true));
    }
}
