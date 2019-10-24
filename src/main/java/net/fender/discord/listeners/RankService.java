package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.fender.BigDecimalSummaryStatistics;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.StatProduct;
import net.fender.pogo.TradeLevel;
import net.fender.pvpoke.Pokemon;

import java.math.BigDecimal;
import java.util.*;

import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static net.fender.pogo.TradeLevel.*;

public class RankService {

    private RankService() { }

    public static void rank(Pokemon pokemon, League league, IndividualValues ivs, TextChannel rankBot) {
        int levelFloor = pokemon.getLevelFloor();
        String pokemonName = pokemon.getSpeciesId();
        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, league);
        if (stats.isEmpty()) {
            ivs = pokemon.isTradable() ? IndividualValues.ZERO : IndividualValues.floorNonTradable(ivs);
            StatProduct statProduct = new StatProduct(pokemon, ivs, levelFloor);
            rankBot.sendMessage(pokemonName + " is ineligible for " + league + " league. Min CP @ lvl " +
                    levelFloor + " is " + statProduct.getCp()).submit();
            return;
        }

        if (!pokemon.isTradable()) {
            ivs = IndividualValues.floorNonTradable(ivs);
        }

        StatProduct statProduct = stats.get(ivs);
        if (statProduct == null) {
            StatProduct over = new StatProduct(pokemon, ivs, levelFloor);
            rankBot.sendMessage("CP at lvl " + levelFloor + " for " + ivs + " is " + over.getCp() +
                    " which is over " + league.maxCp + " for " + league + " league.").submit();
            return;
        }

        Collection<StatProduct> statProducts = stats.values();
        SortedSet<StatProduct> betterStats = statProducts.stream().
                filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
                collect(toCollection(TreeSet::new));
        int rank = betterStats.size() + 1;
        StatProduct wildStats = betterStats.isEmpty() ? statProduct : betterStats.first();
        int bestStatProduct = wildStats.getStatProduct();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(pokemon.getSpeciesId());

        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / wildStats.getStatProduct()) / 10.0;
        String desc = "#" + rank + "/" + stats.size() + " | L" + statProduct.getLevel() + " | CP " +
                statProduct.getCp() + " | " + percentBest + "%";
        embedBuilder.addField(ivs.toString(), desc, false);

        embedBuilder.addField("#1 Rank", getDesc(wildStats, bestStatProduct), false);

        if (pokemon.isTradable() && league != League.master) {
            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(GREAT_FRIEND)).
                    sorted().findFirst().ifPresent(great ->
                    embedBuilder.addField("Top Great Friend Trade:", getDesc(great, bestStatProduct), false));

            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(ULTRA_FRIEND)).
                    sorted().findFirst().ifPresent(ultra ->
                    embedBuilder.addField("Top Ultra Friend Trade:", getDesc(ultra, bestStatProduct), false));

            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(BEST_FRIEND)).
                    sorted().findFirst().ifPresent(best ->
                    embedBuilder.addField("Top Best Friend Trade:", getDesc(best, bestStatProduct), false));

            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(RAID_HATCH_RESEARCH)).
                    sorted().findFirst().ifPresent(raid ->
                    embedBuilder.addField("Top Raid/Hatch/Research:", getDesc(raid, bestStatProduct), false));

            statProducts.stream().
                    filter(sp -> sp.isTradeLevel(LUCKY_TRADE)).
                    sorted().findFirst().ifPresent(lucky ->
                    embedBuilder.addField("Top Lucky Friend Trade:", getDesc(lucky, bestStatProduct), false));
        }

        if (pokemon.isTradable()) {
            long count = betterStats.stream().filter(sp -> sp.isTradeLevel(BEST_FRIEND)).count();
            double odds = Math.round(1000.0 * count / 1331) / 10.0;
            embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
        } else {
            embedBuilder.setDescription("(not tradable)");
        }

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
    }

    public static void summary(Pokemon pokemon, League league, TextChannel rankBot) {
        int levelFloor = pokemon.getLevelFloor();
        String pokemonName = pokemon.getSpeciesId();
        Map<IndividualValues, StatProduct> statProducts = StatProduct.generateStatProducts(pokemon, league);
        if (statProducts.isEmpty()) {
            IndividualValues ivs = pokemon.isTradable() ?
                    IndividualValues.ZERO :
                    IndividualValues.floorNonTradable(IndividualValues.PERFECT);
            StatProduct statProduct = new StatProduct(pokemon, ivs, levelFloor);
            rankBot.sendMessage(pokemonName + " is ineligible for " + league + " league. Min CP @ lvl " +
                    levelFloor + " is " + statProduct.getCp()).submit();
            return;
        }

        List<StatProduct> values = statProducts.values().stream().sorted().collect(toList());
        StatProduct best = values.get(0);
        // divide by 100 for ease of making percentages
        BigDecimal max = BigDecimal.valueOf(best.getStatProduct() / 100.0);

        for (TradeLevel tradeLevel : TradeLevel.REVERSED) {
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
                    "max: " +  maxStatProductPercent + "%; " +
                    "avg: " + averageStatProductPercent + "%; " +
                    "std dev: " + stdDev + "%");
        }

    }

    private static String percent(double d) {
        return (Math.round(d * 10.0) / 10.0) + "%";
    }

    private static String getDesc(StatProduct statProduct, int bestStatProduct) {
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStatProduct) / 10.0;
        return "L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
                statProduct.getIvs().getAttack() + "/" + statProduct.getIvs().getDefense() + "/" +
                statProduct.getIvs().getStamina() + " | " + percentBest + "%";
    }
}
