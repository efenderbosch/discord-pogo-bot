package net.fender;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pogo.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class TestStuff {

    @Test
    public void test() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("bastiodon");
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);
        stats.values().stream().sorted().limit(25).forEach(System.out::println);

        IndividualValues ivs = new IndividualValues(15, 15, 11);
        StatProduct statProduct = stats.get(ivs);

        SortedSet<StatProduct> betterStats = stats.values().stream().
                filter(StatProduct::isRaidHatchResearch).
                filter(s -> s.getStatProduct() >= statProduct.getStatProduct()).
                collect(Collectors.toCollection(TreeSet::new));
        int rank = betterStats.size();
        System.out.println("rank: " + rank + "/" + stats.size());
        StatProduct bestStats = betterStats.first();
        System.out.println(bestStats);

        if (pokemon.isTradable()) {
            List<StatProduct> bestFriends = betterStats.stream().filter(StatProduct::isBestFriend).collect(toList());
            double odds = Math.round(1000.0 * bestFriends.size() / 1331) / 10.0;
            System.out.println(odds);
        }
    }
}
