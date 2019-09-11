package net.fender;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pogo.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static net.fender.pogo.TradeLevel.LUCKY_TRADE;

public class TestStuff {

    @Test
    public void test() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("wormadam-trash");
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.great);

        IndividualValues ivs = new IndividualValues(3, 14, 15);
        StatProduct statProduct = stats.get(ivs);
        System.out.println(statProduct);

        for (TradeLevel tradeLevel : TradeLevel.values()) {
            stats.values().stream().
                    filter(s -> s.isTradeLevel(tradeLevel)).
                    sorted().
                    findAny().
                    ifPresent(v -> System.out.println(tradeLevel + ": " + v));
        }
    }

    @Test
    public void test_all() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        SortedSet<PokemonStatProduct> best = new TreeSet<>();
        for (Pokemon pokemon : pokemonRegistry.getAll()) {
            if (!pokemon.isTradable()) continue;
            Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.great);
            Optional<StatProduct> maybeStatProduct = stats.values().stream().
                    sorted().
                    filter(sp -> sp.getCp() > 1400).
                    limit(50).
                    filter(sp -> sp.isTradeLevel(LUCKY_TRADE)).
                    findFirst();
            if (maybeStatProduct.isPresent()) {
                StatProduct statProduct = maybeStatProduct.get();
                PokemonStatProduct pokemonStatProduct = new PokemonStatProduct(pokemon, statProduct);
                best.add(pokemonStatProduct);
            }
        }

        best.stream().forEach(pokemonStatProduct -> {
            StatProduct statProduct = pokemonStatProduct.getStatProduct();
            System.out.println(" *  " + pokemonStatProduct.getPokemon().getName() + " " + statProduct.getCp());
        });
    }

    @Test
    public void test_top() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("mewtwo-armored");
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.great);
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

    @Test
    public void test_pvp_search_string() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon evolvedForm = pokemonRegistry.getPokeman("gardevoir");
        Pokemon baseForm = pokemonRegistry.getPokeman("ralts");
        Map<IndividualValues, StatProduct> evolvedStats =
                StatProduct.generateStatProducts(evolvedForm, League.great).entrySet().stream().
                        sorted(Map.Entry.comparingByValue()).
                        limit(300).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        EnumSet<Appraisal> allAppraisals = EnumSet.noneOf(Appraisal.class);
        SortedSet<Integer> allCP = new TreeSet<>();
        SortedSet<Integer> allHP = new TreeSet<>();
        int maxLevel = (int) Math.floor(evolvedStats.values().stream().map(StatProduct::getLevel).sorted().findFirst().get());
        for (int level = 1; level <= maxLevel; level++) {
            for (IndividualValues ivs : evolvedStats.keySet()) {
                StatProduct statProduct = new StatProduct(baseForm, ivs, level);
                StatProduct evolved = new StatProduct(evolvedForm, ivs, level);
                if (evolved.getCp() <= 1500) {
                    int cp = statProduct.getCp();
                    allCP.add(cp);
                    int hp = statProduct.getHp();
                    allHP.add(hp);
                    Appraisal appraisal = Appraisal.appraise(ivs);
                    allAppraisals.add(appraisal);
                }
            }
        }

        printRanges(allCP, "cp");
        System.out.println();
        printRanges(allHP, "hp");
        System.out.println();
        System.out.println(allAppraisals.stream().map(Appraisal::getSearchString).collect(Collectors.joining(",")));
    }

    @Test
    public void test_pve_search_string() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        ObjectMapper objectMapper = new ObjectMapper();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(objectMapper, resourceLoader);
        Pokemon mudkip = pokemonRegistry.getPokeman("mudkip");
        EnumSet<Appraisal> allAppraisals = EnumSet.noneOf(Appraisal.class);
        SortedSet<Integer> allCP = new TreeSet<>();
        SortedSet<Integer> allHP = new TreeSet<>();
        for (int d = 14; d <= 14; d++) {
            for (int s = 12; s <= 14; s++) {
                for (double l = 25; l <= 35; l += 0.5) {
                    IndividualValues ivs = new IndividualValues(14, d, s);
                    StatProduct statProduct = new StatProduct(mudkip, ivs, l);
                    int cp = statProduct.getCp();
                    allCP.add(cp);
                    int hp = statProduct.getHp();
                    allHP.add(hp);
                    Appraisal appraisal = Appraisal.appraise(ivs);
                    allAppraisals.add(appraisal);
                }
            }
        }

        printRanges(allCP, "cp");
        System.out.println();
        printRanges(allHP, "hp");
        System.out.println();
        System.out.println(allAppraisals.stream().map(Appraisal::getSearchString).collect(Collectors.joining(",")));
    }

    public static void printRanges(SortedSet<Integer> integers, String prefix) {
        int length = integers.size();
        Integer[] cpArray = integers.toArray(new Integer[length]);
        int idx = 0;
        int idx2 = 0;
        while (idx < length) {
            while (++idx2 < length && cpArray[idx2] - cpArray[idx2 - 1] == 1) ;
            System.out.printf("%s%s-%s,", prefix, cpArray[idx], cpArray[idx2 - 1]);
            idx = idx2;
        }
    }
}
