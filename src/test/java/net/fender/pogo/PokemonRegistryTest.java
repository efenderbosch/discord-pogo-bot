package net.fender.pogo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pvpoke.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PokemonRegistryTest {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ObjectMapper objectMapper = new ObjectMapper().
            disable(FAIL_ON_UNKNOWN_PROPERTIES).
            enable(ALLOW_COMMENTS);
    private final PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);

    public PokemonRegistryTest() throws IOException { }

    @Test
    public void test_find() {
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
        Pokemon melmetal = pokemonRegistry.getPokeman("melmetal");
        assertThat(melmetal.isTradable(), is(true));
    }

    @Test
    public void test_rayquaza_level_floor() {
        Pokemon rayquaza = pokemonRegistry.getPokeman("rayquaza");
        assertThat(rayquaza.getLevelFloor(), is(20));
    }

    @Test
    public void test_kyogre_level_floor() {
        Pokemon kyogre = pokemonRegistry.getPokeman("kyogre");
        assertThat(kyogre.getLevelFloor(), is(15));
    }

    @Test
    public void test_chespin() {
        Pokemon chespin = pokemonRegistry.getPokeman("chespin");
        assertThat(chespin, notNullValue());
    }
}
