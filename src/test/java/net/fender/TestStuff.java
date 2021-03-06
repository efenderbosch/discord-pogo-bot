package net.fender;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.io.LineReader;
import net.fender.pogo.*;
import net.fender.pvpoke.Pokemon;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static net.fender.pogo.League.great;
import static net.fender.pogo.League.ultra;
import static net.fender.pogo.TradeLevel.GREAT_FRIEND;
import static net.fender.pogo.TradeLevel.RAID_HATCH_RESEARCH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStuff {

    private static final Logger LOG = LoggerFactory.getLogger(TestStuff.class);

    private static final ObjectMapper MAPPER = new ObjectMapper().
            disable(FAIL_ON_UNKNOWN_PROPERTIES).
            enable(ALLOW_COMMENTS);

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
    public void test1() {

    }

    @Test
    public void test_purified() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon banette = pokemonRegistry.getPokeman("banette");
        IndividualValues ivs = new IndividualValues(2, 15, 2);
        LinkedHashMap<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(banette, great,
                40);
        StatProduct statProduct = statProducts.get(ivs);
        assertTrue(statProduct.isTradeLevel(GREAT_FRIEND) && statProduct.getLevel() >= 25.0);

        SortedSet<StatProduct> purified = statProducts.values().stream().
                filter(sp -> sp.getLevel() >= 25.0 && sp.isTradeLevel(GREAT_FRIEND)).
                collect(toCollection(TreeSet::new));
        SortedSet<StatProduct> betterPurified = purified.stream().
                filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
                collect(toCollection(TreeSet::new));
        int purifiedRank = betterPurified.size() + 1;
        assertThat(purifiedRank, is(44));
    }

    @Test
    public void test_level41() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("scrafty");
        IndividualValues ivs40 = new IndividualValues(15, 15, 15);
        IndividualValues ivs41 = new IndividualValues(15, 15, 15);

        LinkedHashMap<IndividualValues, StatProduct> statProducts40 = StatProduct.generateStatProducts(pokemon, great,
                40);
        LinkedHashMap<IndividualValues, StatProduct> statProducts41 = StatProduct.generateStatProducts(pokemon, great,
                41);

        statProducts40.values().stream().
                filter(statProduct -> statProduct.isTradeLevel(RAID_HATCH_RESEARCH)).
                sorted(Comparator.reverseOrder()).
                limit(1).findAny().ifPresentOrElse(System.out::println, null);

        StatProduct statProduct40 = statProducts40.get(ivs40);
        System.out.println(statProduct40.getCp() + " " + statProduct40.getLevel() + " " + statProduct40.getStatProduct());
        StatProduct statProduct41 = statProducts41.get(ivs41);
        System.out.println(statProduct41.getCp() + " " + statProduct41.getLevel() + " " + statProduct41.getStatProduct());

        StatProduct top40 = statProducts40.values().iterator().next();
        System.out.println(top40.getCp() + " " + top40.getIvs() + " " + top40.getLevel() + " " + top40.getStatProduct());
        StatProduct top41 = statProducts41.values().iterator().next();
        System.out.println(top41.getCp() + " " + top41.getIvs() + " " + top41.getLevel() + " " + top41.getStatProduct());

        SortedSet<StatProduct> all = new TreeSet<>();
        all.addAll(statProducts40.values());
        all.addAll(statProducts41.values());
        long rank40 =
                all.stream().filter(statProduct -> statProduct.getStatProduct() >= top40.getStatProduct()).count();
        System.out.println(rank40);
        long rank41 =
                all.stream().filter(statProduct -> statProduct.getStatProduct() >= top41.getStatProduct()).count();
        System.out.println(rank41);

        long rank =
                statProducts40.values().stream().
                        filter(statProduct -> statProduct.getStatProduct() >= statProduct41.getStatProduct()).count();
        System.out.println(rank);
    }

    @Disabled
    public void test() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        for (Pokemon pokemon : pokemonRegistry.getAll()) {
            LinkedHashMap<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon,
                    great, 40);
            statProducts.values().stream().
                    limit(50).
                    filter(sp -> sp.isTradeLevel(TradeLevel.LUCKY_TRADE) && sp.getCp() > 1400).
                    findFirst().
                    ifPresent(sp -> System.out.println(pokemon.getSpeciesId() + "," + sp.getCp() + "," + sp.getLevel() +
                            "," + sp.getIvs() + "," + sp.getStatProduct()));
//                    filter(sp -> sp.getLevelDefense() > 140 && sp.getHp() > 128).
//                    sorted().limit(100).
//                    forEach(System.out::println);
        }
    }

    @Disabled
    public void test_all() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource gamemaster = resourceLoader.getResource("GAME_MASTER2.json");
        LineReader lineReader =
                new LineReader(new InputStreamReader(resourceLoader.getResource("thrifty_exclude.txt").getInputStream()));
        Set<String> ineligible = new HashSet<>();
        String banned = lineReader.readLine();
        while (banned != null) {
            ineligible.add(banned);
            banned = lineReader.readLine();
        }

        JsonNode root = objectMapper.readTree(gamemaster.getInputStream());
        ArrayNode itemTemplates = (ArrayNode) root.get("itemTemplates");

        for (JsonNode itemTemplate : itemTemplates) {
            if (!itemTemplate.hasNonNull("pokemonSettings")) continue;
            JsonNode pokemonSettings = itemTemplate.get("pokemonSettings");
            //String pokemonId = pokemonSettings.get("pokemonId").asText().toLowerCase();

            if (!pokemonSettings.hasNonNull("form")) continue;

            JsonNode form = pokemonSettings.get("form");
            String pokemonId = form.asText().toLowerCase();
            if (pokemonId.endsWith("_shadow")) continue;

            if (!pokemonSettings.hasNonNull("thirdMove")) continue;
            JsonNode thirdMove = pokemonSettings.get("thirdMove");

            if (!thirdMove.hasNonNull("stardustToUnlock")) continue;
            int stardustToUnlock = thirdMove.get("stardustToUnlock").intValue();
            if (stardustToUnlock > 75001) {
                ineligible.add(pokemonId);
            }
        }
        // allow the discount babies
        ineligible.remove("lucario");
        ineligible.remove("mantine");
        ineligible.remove("sudowoodo");
        ineligible.remove("magmar");
        ineligible.remove("magmortar");
        ineligible.remove("electivire");

        StringJoiner stringJoiner = new StringJoiner(",");
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        for (Pokemon pokemon : pokemonRegistry.getAll()) {
            if (pokemon.getDex() > 649) continue;
            if (pokemon.getTags().contains("mythical") || pokemon.getTags().contains("legendary")) continue;
            if (ineligible.contains(pokemon.getSpeciesId())) {
                //LOG.info("{} cost is high", pokemon.getSpeciesId());
                continue;
            }

            Set<String> chargedMoves = pokemon.getChargedMoves();
            chargedMoves.remove("frustration");
            chargedMoves.remove("return");
            chargedMoves.removeAll(pokemon.getLegacyMoves());
            if (chargedMoves.size() > 7) {
                LOG.info("{} has {} charge moves, skipping", pokemon.getSpeciesId(), chargedMoves.size());
                continue;
            }

            Optional<StatProduct> maybeStatProduct =
                    StatProduct.generateStatProducts(pokemon, ultra, 40).values().stream().findFirst();
            if (!maybeStatProduct.isPresent()) {
                LOG.info("{} is not great league eligible, skipping", pokemon.getSpeciesId());
                continue;
            }

            StatProduct statProduct = maybeStatProduct.get();
            if (statProduct.getLevel() > 34.5) {
                //LOG.info("{} optimum stat product is above 31, skipping", pokemon.getSpeciesId());
                continue;
            }
            stringJoiner.add(pokemon.getSpeciesId());
            //LOG.info("{}", pokemon.getSpeciesId());
        }
        System.out.println(stringJoiner);
    }

    @Test
    public void test_pvp_search_string() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon evolvedForm = pokemonRegistry.getPokeman("empoleon");
        Pokemon baseForm = pokemonRegistry.getPokeman("piplup");
        League league = ultra;
        Map<IndividualValues, StatProduct> evolvedStats =
                StatProduct.generateStatProducts(evolvedForm, league, 40).entrySet().stream().
                        sorted(comparingByValue()).
                        limit(141).
                        collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        EnumSet<Appraisal> allAppraisals = EnumSet.noneOf(Appraisal.class);
        SortedSet<Integer> allCP = new TreeSet<>();
        SortedSet<Integer> allHP = new TreeSet<>();
        int maxLevel = (int) Math.floor(evolvedStats.values().stream().map(StatProduct::getLevel).sorted().findFirst().get());
        for (int level = 1; level <= maxLevel; level++) {
            for (IndividualValues ivs : evolvedStats.keySet()) {
                StatProduct statProduct = new StatProduct(baseForm, ivs, level);
                StatProduct evolved = new StatProduct(evolvedForm, ivs, level);
                if (evolved.getCp() <= league.maxCp) {
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

    @Test
    public void test_pvp() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        PokemonRegistry pokemonRegistry = new PokemonRegistry(MAPPER, resourceLoader);
        Pokemon pokemon = pokemonRegistry.getPokeman("wigglytuff");
        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, great, 40);
        StatProduct max = statProducts.values().iterator().next();
        //double max = statProducts.values().stream().mapToDouble(StatProduct::getStatProduct).max().getAsDouble();
        double bestAtk =
                statProducts.values().stream().limit(500).mapToDouble(StatProduct::getLevelAttack).max().getAsDouble();
        System.out.println(bestAtk);

        double bestDef = statProducts.values().stream().limit(500).mapToDouble(StatProduct::getLevelDefense).max().getAsDouble();
        System.out.println(bestDef);

        statProducts.values().stream().
//                filter(sp -> sp.getStatProduct() > 1765496).
//                filter(sp -> sp.getCp() <= 1476)
                filter(sp -> sp.getLevelAttack() > 114.91).
                filter(sp -> sp.getLevelDefense() >= 75.47).
                //count();
//                 filter(sp -> sp.getHp() >= 140).
//                        sorted().
        forEach(sp -> System.out.println(sp.getCp() + " " + sp.getIvs() + " " +
        StatProduct.round(sp.getStatProduct() * 100.0 / max.getStatProduct()) + "%"));
//            System.out.println(count);
    }
}
