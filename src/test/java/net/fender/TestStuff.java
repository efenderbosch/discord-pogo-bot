package net.fender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.fender.pogo.*;
import net.fender.pvpoke.Pokemon;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
import static net.fender.pogo.League.great;

public class TestStuff {

    private static final ObjectMapper MAPPER = new ObjectMapper().
            disable(FAIL_ON_UNKNOWN_PROPERTIES).
            enable(ALLOW_COMMENTS);

    @Test
    public void test_asdf() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon banette = pokemonRegistry.getPokeman("banette");
        LinkedHashMap<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(banette, great);
        statProducts.values().stream().
                filter(sp -> sp.getLevel() >= 25.0 &&
                        sp.getIvs().getAttack() >= 2 &&
                        sp.getIvs().getDefense() >= 2 &&
                        sp.getIvs().getStamina() >= 2).
                sorted(Comparator.reverseOrder()).
                limit(25).
                forEach(sp -> System.out.println(sp.getIvs().getAttack() + "/" + sp.getIvs().getDefense() + "/" + sp.getIvs().getStamina()));
    }

    @Test
    public void test() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode gameMaster = mapper.readTree(resourceLoader.getResource("GAME_MASTER2.json").getInputStream());
        ArrayNode itemTemplates = (ArrayNode) gameMaster.get("itemTemplates");
        Set<String> excludes = new HashSet<>();
        Set<String> purified = new HashSet<>();
        for (JsonNode node : itemTemplates) {
            JsonNode pokemonSettings = node.get("pokemonSettings");
            if (pokemonSettings == null || pokemonSettings.isNull()) continue;

            if (pokemonSettings.hasNonNull("shadow")) continue;

            JsonNode thirdMove = pokemonSettings.get("thirdMove");
            if (thirdMove == null || thirdMove.isNull()) continue;

            JsonNode stardustToUnlock = thirdMove.get("stardustToUnlock");
            if (stardustToUnlock == null || stardustToUnlock.isNull()) continue;

            if (stardustToUnlock.intValue() >= 60_000) {
                JsonNode form = pokemonSettings.get("form");
                String formName;
                if (form != null && !form.isNull()) {
                    formName = form.textValue().toLowerCase();
                } else {
                    formName = pokemonSettings.get("pokemonId").textValue().toLowerCase();
                }
                if (formName.endsWith("_purified")) {
                    purified.add(StringUtils.substringBefore(formName, "_purified"));
                } else {
                    excludes.add(formName);
                }
            }

        }
        excludes.removeAll(purified);
        Scanner scanner = new Scanner(resourceLoader.getResource("thrifty_exclude.txt").getInputStream());
        while (scanner.hasNext()) {
            excludes.add(scanner.nextLine());
        }

        StringJoiner joiner = new StringJoiner(",");
        for (Pokemon pokemon : pokemonRegistry.getAll()) {
            if (pokemon.getDex() > 637) continue;

            if (excludes.contains(pokemon.getSpeciesId())) continue;

            Set<String> chargeMoves = pokemon.getChargedMoves();
            chargeMoves.remove("FRUSTRATION");
            chargeMoves.remove("RETURN");
            chargeMoves.removeAll(pokemon.getLegacyMoves());
            if (chargeMoves.size() > 4) continue;

            LinkedHashMap<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, great);
            Collection<StatProduct> values = statProducts.values();
            if (values.isEmpty()) continue;
            double level =
                    values.stream().limit(1).mapToDouble(statProduct -> statProduct.getLevel()).max().getAsDouble();
            if (level <= 30.5) {
                joiner.add(pokemon.getSpeciesId());
            }
        }
        System.out.println(joiner);
//        LinkedHashMap<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, great);
//        Map<IndividualValues, StatProduct> vaporeon = statProducts.entrySet().stream().
//                limit(256).
//                collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//
//        pokemon = pokemonRegistry.getPokeman("flareon");
//        statProducts = StatProduct.generateStatProducts(pokemon, great);
//        Map<IndividualValues, StatProduct> flareon = statProducts.entrySet().stream().
//                sorted(Map.Entry.comparingByValue()).
//                limit(256).
//                collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//
//        pokemon = pokemonRegistry.getPokeman("jolteon");
//        statProducts = StatProduct.generateStatProducts(pokemon, great);
//        Map<IndividualValues, StatProduct> jolteon = statProducts.entrySet().stream().
//                sorted(Map.Entry.comparingByValue()).
//                limit(256).
//                collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
//                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//
//        Set<IndividualValues> ivs = vaporeon.keySet();
//        ivs.retainAll(flareon.keySet());
//        ivs.retainAll(jolteon.keySet());
//        for (IndividualValues iv : ivs) {
//            StatProduct vape = vaporeon.get(iv);
//            StatProduct flare = flareon.get(iv);
//            StatProduct jolt = jolteon.get(iv);
//            System.out.println(iv + " v: " + vape.get);
//        }

//        double top = values.get(0).getStatProduct() * 1.0;
//        long count = values.stream().
////                filter(sp -> TradeLevel.GREAT_FRIEND.test(sp.getIvs())).
//        filter(sp -> sp.getLevel() >= 20.0).count();
//        System.out.println(count);
//
////        forEach(sp -> System.out.println(" * " + sp.getIvs() + "; cp: " + sp.getCp() + "; lvl: " + sp.getLevel() +
////                        "; atk: " + StatProduct.round(sp.getLevelAttack()) + "; def: " + StatProduct.round(sp.getLevelDefense())
////                        + "; hp: " + sp.getHp() + "; " + Math.round(1000.0 * sp .getStatProduct() / top) / 10.0 + "%"));
//        long better = values.stream().filter(sp -> sp.getStatProduct() > statProduct.getStatProduct()).count();
//        System.out.println(better);
//        // divide by 100 for ease of making percentages
//        BigDecimal max = BigDecimal.valueOf(values.get(0).getStatProduct() / 100.0);
//
//        for (TradeLevel tradeLevel : TradeLevel.values()) {
//            values.stream().
//                    filter(s -> s.isTradeLevel(tradeLevel)).
//                    sorted().
//                    findAny().
//                    ifPresent(v -> System.out.println(tradeLevel + ": " + v));
//
//            BigDecimalSummaryStatistics stats = statProducts.values().stream().
//                    filter(s -> s.isTradeLevel(tradeLevel)).
//                    map(StatProduct::getStatProduct).
//                    collect(BigDecimalSummaryStatistics.DECIMAL64,
//                            BigDecimalSummaryStatistics::accept,
//                            BigDecimalSummaryStatistics::combine);
//
//            BigDecimal minStatProductPercent = stats.getMin().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
//            BigDecimal maxStatProductPercent = stats.getMax().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
//            BigDecimal averageStatProductPercent = stats.getAverage().divide(max, DECIMAL64).setScale(1, HALF_EVEN);
//            BigDecimal stdDev = stats.getStandardDeviation().divide(max, DECIMAL64).setScale(2, HALF_EVEN);
//            System.out.println("\t min: " + minStatProductPercent + "%; " +
//                    "max: " + maxStatProductPercent + "%; " +
//                    "avg: " + averageStatProductPercent + "%; " +
//                    "std dev: " + stdDev + "%");
//        }
    }

    @Test
    public void test_all() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        SortedSet<PokemonStatProduct> best = new TreeSet<>();
        SortedSet<Pokemon> sorted = new TreeSet<>(Comparator.comparing(Pokemon::getDex));
        sorted.addAll(pokemonRegistry.getAll());
        Map<Pokemon, StatProduct> filtered = new HashMap<>();
        for (Pokemon pokemon : sorted) {
            //if (!pokemon.isTradable()) continue;
            if (!pokemon.getTypes().contains("ghost")) continue;
            Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, great);
            Optional<StatProduct> maybeStatProduct = stats.values().stream().
                    sorted().
                    filter(sp -> sp.getCp() > 1260).
                    //limit(50).
                    //filter(sp -> sp.isTradeLevel(LUCKY_TRADE)).
                            findAny();
            if (maybeStatProduct.isPresent()) {
                StatProduct statProduct = maybeStatProduct.get();
                filtered.put(pokemon, statProduct);
//                System.out.println(pokemon.getSpeciesId() + ": " + statProduct.getCp() + "@" + statProduct.getLevel() +
//                        " " + statProduct.getIvs() + " " + statProduct.getStatProduct());
                //PokemonStatProduct pokemonStatProduct = new PokemonStatProduct(pokemon, statProduct);
                //best.add(pokemonStatProduct);
            }
        }

        Map<Pokemon, StatProduct> sorted2 = filtered.entrySet().stream().
                sorted(reverseOrder(comparingByValue())).
                collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        for (Map.Entry<Pokemon, StatProduct> entry : sorted2.entrySet()) {
            Pokemon pokemon = entry.getKey();
            StatProduct statProduct = entry.getValue();
            System.out.println(pokemon.getSpeciesId() + ": \t\t" + statProduct.getCp() + "@" + statProduct.getLevel() +
                    "; atk: " + StatProduct.round(statProduct.getLevelAttack()) +
                    ", def: " + StatProduct.round(statProduct.getLevelDefense()) +
                    ", hp: " + statProduct.getHp() + "; " + statProduct.getStatProduct());
        }


//        best.stream().forEach(pokemonStatProduct -> {
//            StatProduct statProduct = pokemonStatProduct.getStatProduct();
//            System.out.println(" *  " + pokemonStatProduct.getPokemon() + " " + statProduct.getCp());
//        });
    }

    @Test
    public void test_pvp_search_string() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon evolvedForm = pokemonRegistry.getPokeman("infernape");
        Pokemon baseForm = pokemonRegistry.getPokeman("chimchar");
        Map<IndividualValues, StatProduct> evolvedStats =
                StatProduct.generateStatProducts(evolvedForm, great).entrySet().stream().
                        sorted(comparingByValue()).
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
        Pokemon pokemon = pokemonRegistry.getPokeman("dragonair");
        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, great);
        double max = statProducts.values().stream().mapToDouble(StatProduct::getStatProduct).max().getAsDouble();
        statProducts.values().stream().
                filter(sp -> sp.getLevelAttack() >= 116.16).
                filter(sp -> sp.getLevelDefense() >= 112.78).
                //filter(sp -> sp.getHp() >= 158).
                        sorted().
                forEach(sp -> System.out.println(sp.getIvs() + " " + StatProduct.round(sp.getStatProduct() * 100.0 / max) +
                        "%"));
    }
}
