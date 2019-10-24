package net.fender;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fender.pogo.*;
import net.fender.pvpoke.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class TestStuff {

    private static final ObjectMapper MAPPER = new ObjectMapper().
            disable(FAIL_ON_UNKNOWN_PROPERTIES).
            enable(ALLOW_COMMENTS);

    //@Test
    public void test() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("banette");
        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, League.great);
        System.out.println(statProducts.size());

        IndividualValues ivs = new IndividualValues(0, 0, 0);
        StatProduct statProduct = statProducts.get(ivs);
        System.out.println(statProduct);
        List<StatProduct> values = statProducts.values().stream().sorted().collect(toList());
        double top = values.get(0).getStatProduct() * 1.0;
        long count = values.stream().
                filter(sp -> TradeLevel.GREAT_FRIEND.test(sp.getIvs())).
                filter(sp -> sp.getLevel() >= 25.0).count();

//        forEach(sp -> System.out.println(" * " + sp.getIvs() + "; cp: " + sp.getCp() + "; lvl: " + sp.getLevel() +
//                        "; atk: " + StatProduct.round(sp.getLevelAttack()) + "; def: " + StatProduct.round(sp.getLevelDefense())
//                        + "; hp: " + sp.getHp() + "; " + Math.round(1000.0 * sp .getStatProduct() / top) / 10.0 + "%"));
        long better = values.stream().filter(sp -> sp.getStatProduct() > statProduct.getStatProduct()).count();
        System.out.println(better);
        // divide by 100 for ease of making percentages
        BigDecimal max = BigDecimal.valueOf(values.get(0).getStatProduct() / 100.0);

        for (TradeLevel tradeLevel : TradeLevel.values()) {
            values.stream().
                    filter(s -> s.isTradeLevel(tradeLevel)).
                    sorted().
                    findAny().
                    ifPresent(v -> System.out.println(tradeLevel + ": " + v));

            BigDecimalSummaryStatistics stats = statProducts.values().stream().
                    filter(s -> s.isTradeLevel(tradeLevel)).
                    map(StatProduct::getStatProduct).
                    collect(BigDecimalSummaryStatistics.DECIMAL64,
                            BigDecimalSummaryStatistics::accept,
                            BigDecimalSummaryStatistics::combine);

            BigDecimal minStatProductPercent = stats.getMin().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
            BigDecimal maxStatProductPercent = stats.getMax().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
            BigDecimal averageStatProductPercent = stats.getAverage().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
            BigDecimal stdDev = stats.getStandardDeviation().divide(max, DECIMAL64).setScale(2, HALF_EVEN);
            System.out.println("\t min: " + minStatProductPercent + "%; " +
                    "max: " + maxStatProductPercent + "%; " +
                    "avg: " + averageStatProductPercent + "%; " +
                    "std dev: " + stdDev + "%");
        }
    }

    //@Test
    public void test_all() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        SortedSet<PokemonStatProduct> best = new TreeSet<>();
        SortedSet<Pokemon> sorted = new TreeSet<>(Comparator.comparing(Pokemon::getDex));
        sorted.addAll(pokemonRegistry.getAll());
        for (Pokemon pokemon : sorted) {
            if (!pokemon.isTradable()) continue;
            Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.great);
            Optional<StatProduct> maybeStatProduct = stats.values().stream().
                    sorted().
                    filter(sp -> sp.getCp() > 1200).
                    //limit(50).
                    //filter(sp -> sp.isTradeLevel(LUCKY_TRADE)).
                            findAny();
            if (maybeStatProduct.isPresent()) {
                StatProduct statProduct = maybeStatProduct.get();
                System.out.println(pokemon.getSpeciesId() + "," + statProduct.getLevel() + "," + statProduct.getIvs() + "," + TradeLevel.getTradeLevel(statProduct.getIvs()).description);
                //PokemonStatProduct pokemonStatProduct = new PokemonStatProduct(pokemon, statProduct);
                //best.add(pokemonStatProduct);
            }
        }

        best.stream().forEach(pokemonStatProduct -> {
            StatProduct statProduct = pokemonStatProduct.getStatProduct();
            System.out.println(" *  " + pokemonStatProduct.getPokemon() + " " + statProduct.getCp());
        });
    }

    @Test
    public void test_pvp_search_string() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon evolvedForm = pokemonRegistry.getPokeman("shelgon");
        Pokemon baseForm = pokemonRegistry.getPokeman("trapinch");
        Map<IndividualValues, StatProduct> evolvedStats =
                StatProduct.generateStatProducts(evolvedForm, League.great).entrySet().stream().
                        sorted(Map.Entry.comparingByValue()).
                        limit(88).
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
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
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
            if (cpArray[idx] != cpArray[idx2 - 1]) {
                System.out.printf("%s%s-%s,", prefix, cpArray[idx], cpArray[idx2 - 1]);
            } else {
                System.out.printf("%s%s,", prefix, cpArray[idx], cpArray[idx2 - 1]);
            }
            idx = idx2;
        }
    }

    @Test
    public void test_pvp() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("skuntank");
        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, League.great);
        double max = statProducts.values().stream().mapToInt(StatProduct::getStatProduct).max().getAsInt();
        statProducts.values().stream().
                filter(sp -> sp.getLevelDefense() >= 92.2).
                filter(sp -> sp.getHp() >= 158).
                sorted().
                forEach(sp -> System.out.println(sp.getIvs() + " " + StatProduct.round(sp.getStatProduct() / max) +
                        "%"));
    }
}
