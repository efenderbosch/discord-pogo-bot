package net.fender;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pogo.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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

    @Disabled
    public void test_all() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        SortedSet<PokemonStatProduct> best = new TreeSet<>();
        for (Pokemon pokemon : pokemonRegistry.getAll()) {
            Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);
            StatProduct statProduct = stats.values().stream().sorted().findFirst().get();
            PokemonStatProduct pokemonStatProduct = new PokemonStatProduct(pokemon, statProduct);
            best.add(pokemonStatProduct);
        }
        best.stream().limit(50).forEach(pokemonStatProduct -> {
            StatProduct statProduct = pokemonStatProduct.getStatProduct();
            int fakeCp = (int) (statProduct.getStatProduct() / 2815080.0 * 1500.0);
            System.out.println(pokemonStatProduct.getPokemon().getName() + " " +
                    statProduct.getStatProduct() + " @ " +
                    statProduct.getLevel() + " " + fakeCp);
        });
    }

    @Test
    public void test_top() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("vigoroth");
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);
        int size = stats.size() / 8;
        List<StatProduct> top = stats.values().stream().sorted().limit(size).collect(toList());

        long amazes = top.stream().filter(StatProduct::isAmazes).count();
        long strong = top.stream().filter(StatProduct::isStrong).count();
        long decent = top.stream().filter(StatProduct::isDecent).count();
        long notGreatInBattle = top.stream().filter(StatProduct::isNotGreatInBattle).count();
        long attackTop = top.stream().filter(StatProduct::isAttackBest).count();

        System.out.println("amazes:    " + round(100.0 * amazes / size) + "%");
        System.out.println("strong:    " + round(100.0 * strong / size) + "%");
        System.out.println("decent:    " + round(100.0 * decent / size) + "%");
        System.out.println("not great: " + round(100.0 * notGreatInBattle / size) + "%");
        System.out.println("atk best : " + round(100.0 * attackTop / size) + "%");

        Map<Integer, Long> counts = top.stream().collect(groupingBy(StatProduct::getCp, counting()));
        long breakpoint = size / 16;
        long sum = 0;
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            int cp = entry.getKey();
            long count = entry.getValue();
            sum += count;
            if (sum >= breakpoint) {
                System.out.println("eval threshold: " + cp);
                break;
            }
        }
    }

    private static double round(double d) {
        return Math.round(d * 10.0) / 10.0;
    }
}
