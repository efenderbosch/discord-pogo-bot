package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.StatProduct;
import net.fender.pvpoke.Pokemon;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.stream.Collectors.toCollection;
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
//        User author = event.getAuthor();
//        embedBuilder.setFooter(author.getName(), author.getAvatarUrl());
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

        // put this in a summary command
//        int size = stats.size() / 8;
//        List<StatProduct> top = stats.values().stream().sorted().limit(size).collect(toList());
//        long amazes = top.stream().filter(StatProduct::isAmazes).count();
//        embedBuilder.addField("Top Appraisal", percent(100.0 * amazes / size), true);
//        long strong = top.stream().filter(StatProduct::isStrong).count();
//        embedBuilder.addField("High Appraisal", percent(100.0 * strong / size), false);
//        long decent = top.stream().filter(StatProduct::isDecent).count();
//        embedBuilder.addField("Average Appraisal", percent(100.0 * decent / size), true);
//        long notGreatInBattle = top.stream().filter(StatProduct::isNotGreatInBattle).count();
//        embedBuilder.addField("Poor Appraisal", percent(100.0 * notGreatInBattle / size), false);
//        long attackTop = top.stream().filter(StatProduct::isAttackBest).count();
//        embedBuilder.addField("Attack Best Stat", percent(100.0 * attackTop / size), true);
//
//        Map<Integer, Long> counts = top.stream().collect(groupingBy(StatProduct::getCp, counting()));
//        long breakpoint = size / 16;
//        long sum = 0;
//        long cpBreakpoint = 1500;
//        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
//            int cp = entry.getKey();
//            long count = entry.getValue();
//            sum += count;
//            if (sum >= breakpoint) {
//                cpBreakpoint = cp;
//                break;
//            }
//        }
//        embedBuilder.addField("CP Eval Breakpoint", cpBreakpoint + "", false);

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
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
