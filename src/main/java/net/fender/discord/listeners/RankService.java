package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.StatProduct;
import net.fender.pvpoke.Pokemon;

import java.util.*;

import static java.util.stream.Collectors.toCollection;
import static net.fender.pogo.TradeLevel.BEST_FRIEND;
import static net.fender.pogo.TradeLevel.GREAT_FRIEND;

public class RankService {

    private RankService() { }

    public static void rank(Pokemon pokemon, League league, IndividualValues ivs, TextChannel rankBot) {
        int levelFloor = pokemon.getLevelFloor();
        String pokemonName = pokemon.getSpeciesId();
        Map<IndividualValues, StatProduct> level40Stats = StatProduct.generateStatProducts(pokemon, league, 40);
        if (level40Stats.isEmpty()) {
            ivs = pokemon.isTradable() ? IndividualValues.ZERO : IndividualValues.floorNonTradable(ivs);
            StatProduct statProduct = new StatProduct(pokemon, ivs, levelFloor);
            rankBot.sendMessage(pokemonName + " is ineligible for " + league + " league. Min CP @ lvl " +
                    levelFloor + " is " + statProduct.getCp()).submit();
            return;
        }
        Map<IndividualValues, StatProduct> level45Stats = StatProduct.generateStatProducts(pokemon, league, 45);

        if (!pokemon.isTradable()) {
            ivs = IndividualValues.floorNonTradable(ivs);
        }

        StatProduct level40StatProduct = level40Stats.get(ivs);
        StatProduct level45StatProduct = level45Stats.get(ivs);
        if (level40StatProduct == null) {
            StatProduct over = new StatProduct(pokemon, ivs, levelFloor);
            rankBot.sendMessage("CP at lvl " + levelFloor + " for " + ivs + " is " + over.getCp() +
                    " which is over " + league.maxCp + " for " + league + " league.").submit();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(pokemon.getSpeciesId());
        buildEmbed(pokemon, level40StatProduct, level40Stats.values(), league, embedBuilder);
        StatProduct topLevel40 = level40Stats.entrySet().stream().
                findFirst().
                map(Map.Entry::getValue).
                orElseGet(null);
        StatProduct topLevel45 = level45Stats.entrySet().stream().
                findFirst().
                map(Map.Entry::getValue).
                orElseGet(null);

        if (!Objects.equals(topLevel40, topLevel45)) {
            embedBuilder.addField("", "------------", false);
            buildEmbed(pokemon, level45StatProduct, level45Stats.values(), league, embedBuilder);
        }

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
    }

    private static void buildEmbed(Pokemon pokemon,
                                   StatProduct statProduct,
                                   Collection<StatProduct> statProducts,
                                   League league,
                                   EmbedBuilder embedBuilder) {
        SortedSet<StatProduct> betterStats = statProducts.stream().
                filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
                collect(toCollection(TreeSet::new));
        int rank = betterStats.size() + 1;
        StatProduct wildStats = betterStats.isEmpty() ? statProduct : betterStats.first();
        double bestStatProduct = wildStats.getStatProduct();

        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / wildStats.getStatProduct()) / 10.0;
        String desc = "#" + rank + "/" + statProducts.size() + " | L" + statProduct.getLevel() + " | CP " +
                statProduct.getCp() + " | " + percentBest + "%";

        embedBuilder.addField(statProduct.getIvs().toString(), desc, false);
        embedBuilder.addField("atk", StatProduct.round(statProduct.getLevelAttack()), true);
        embedBuilder.addField("def", StatProduct.round(statProduct.getLevelDefense()), true);
        embedBuilder.addField("hp", String.valueOf(statProduct.getHp()), true);

        // great friend and purified are the same
        if (statProduct.isTradeLevel(GREAT_FRIEND) && statProduct.getLevel() >= 25.0) {
            SortedSet<StatProduct> purified = statProducts.stream().
                    filter(sp -> sp.getLevel() >= 25.0 && sp.isTradeLevel(GREAT_FRIEND)).
                    collect(toCollection(TreeSet::new));
            SortedSet<StatProduct> betterPurified = purified.stream().
                    filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
                    collect(toCollection(TreeSet::new));
            int purifiedRank = betterPurified.size() + 1;
            String purifiedDesc = "#" + purifiedRank + "/" + purified.size();
            embedBuilder.addField("Purified rank", purifiedDesc, true);
        }

        embedBuilder.addField("#1 Rank", getDesc(wildStats, bestStatProduct), false);

        if (pokemon.isTradable() && league != League.master) {
//            statProducts.stream().
//                    filter(sp -> sp.isTradeLevel(GREAT_FRIEND)).
//                    sorted().findFirst().ifPresent(great ->
//                    embedBuilder.addField("Top Great Friend Trade:", getDesc(great, bestStatProduct), false));

//            statProducts.stream().
//                    filter(sp -> sp.isTradeLevel(ULTRA_FRIEND)).
//                    sorted().findFirst().ifPresent(ultra ->
//                    embedBuilder.addField("Top Ultra Friend Trade:", getDesc(ultra, bestStatProduct), false));

            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(BEST_FRIEND)).
                    sorted().findFirst().ifPresent(best ->
                    embedBuilder.addField("Top Best Friend Trade:", getDesc(best, bestStatProduct), false));

//            statProducts.stream().
//                    filter(sp -> sp.isTradeLevel(RAID_HATCH_RESEARCH)).
//                    sorted().findFirst().ifPresent(raid ->
//                    embedBuilder.addField("Top Raid/Hatch/Research:", getDesc(raid, bestStatProduct), false));

//            statProducts.stream().
//                    filter(sp -> sp.isTradeLevel(LUCKY_TRADE)).
//                    sorted().findFirst().ifPresent(lucky ->
//                    embedBuilder.addField("Top Lucky Friend Trade:", getDesc(lucky, bestStatProduct), false));
        }

        if (pokemon.isTradable()) {
            long count = betterStats.stream().filter(sp -> sp.isTradeLevel(BEST_FRIEND)).count();
            double odds = Math.round(1000.0 * count / 1331) / 10.0;
            embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
        } else {
            embedBuilder.setDescription("(not tradable)");
        }
    }

//    public static void summary(Pokemon pokemon, League league, TextChannel rankBot) {
//        int levelFloor = pokemon.getLevelFloor();
//        String pokemonName = pokemon.getSpeciesId();
//        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, league);
//        if (statProducts.isEmpty()) {
//            IndividualValues ivs = pokemon.isTradable() ?
//                    IndividualValues.ZERO :
//                    IndividualValues.floorNonTradable(IndividualValues.PERFECT);
//            StatProduct statProduct = new StatProduct(pokemon, ivs, levelFloor);
//            rankBot.sendMessage(pokemonName + " is ineligible for " + league + " league. Min CP @ lvl " +
//                    levelFloor + " is " + statProduct.getCp()).submit();
//            return;
//        }
//
//        List<StatProduct> values = statProducts.values().stream().sorted().collect(toList());
//        StatProduct best = values.get(0);
//        // divide by 100 for ease of making percentages
//        BigDecimal max = BigDecimal.valueOf(best.getStatProduct() / 100.0);
//
//        for (TradeLevel tradeLevel : TradeLevel.REVERSED) {
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
//
//    }

    private static String percent(double d) {
        return (Math.round(d * 10.0) / 10.0) + "%";
    }

    private static String getDesc(StatProduct statProduct, double bestStatProduct) {
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStatProduct) / 10.0;
        return "L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
                statProduct.getIvs().getAttack() + "/" + statProduct.getIvs().getDefense() + "/" +
                statProduct.getIvs().getStamina() + " | " + percentBest + "%";
    }
}
