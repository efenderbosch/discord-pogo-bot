package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.PokemonRegistry;
import net.fender.pvpoke.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fender.discord.listeners.RankEmoteListener.REACTIONS;

@Component
public class RankListener extends CommandEventWithHelpListener {

    static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter("rank-bot");

    private static final Pattern BASIC = Pattern.compile("\\$rank.*");
    private static final String REG_EX =
            "\\$rank\\s+" +
            "(?<pokemon>[-\\w]+)\\s+" +
            "(?<ivs>" + IndividualValues.IVS_REG_EX + ")\\s*" +
            "(?<league>great|ultra|master)?";
    private static final Pattern FULL = Pattern.compile(REG_EX);

    private final PokemonRegistry pokemonRegistry;
    private TextChannel rankBot;

    @Autowired
    public RankListener(PokemonRegistry pokemonRegistry) {
        super(BASIC, FULL, CHANNEL_NAME_FILTER);
        this.pokemonRegistry = pokemonRegistry;
    }

    @Override
    protected void doCommand(MessageReceivedEvent event, Matcher matcher) {
        JDA jda = event.getJDA();
        if (rankBot == null) {
            rankBot = jda.getTextChannelsByName("rank-bot", true).get(0);
        }
        rankBot.sendTyping().submit();

        League league = League.great;
        String leagueName = matcher.group("league");
        if (leagueName != null && !leagueName.trim().isEmpty()) {
            Optional<League> maybeLeague = League.find(leagueName);
            if (!maybeLeague.isPresent()) {
                rankBot.sendMessage("Unknown league '" + leagueName + "'!").submit();
                sendHelp(event, matcher);
                return;
            }
            league = maybeLeague.get();
        }

        String pokemonName = matcher.group("pokemon").toLowerCase();
        String ivGroup = matcher.group("ivs");
        if (ivGroup == null) {
            // send summary
            rankBot.sendMessage("summary goes here").submit();
            return;
        }

        IndividualValues ivs = IndividualValues.parse(ivGroup);

        // reg ex won't pass if these aren't numbers, so should not need try/catch around conversion from String
//        int atk = Math.max(0, Math.min(15, Integer.parseInt(attributes[0])));
//        int def = Math.max(0, Math.min(15, Integer.parseInt(attributes[1])));
//        int sta = Math.max(0, Math.min(15, Integer.parseInt(attributes[2])));
//        IndividualValues ivs = new IndividualValues(atk, def, sta);

        Pokemon pokemon = pokemonRegistry.getPokeman(pokemonName);
        if (pokemon == null) {
            List<String> found = pokemonRegistry.find(pokemonName);
            if (found.isEmpty()) {
                rankBot.sendMessage(pokemonName + " not found!").submit();
                sendHelp(event, matcher);
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(pokemonName + " not found! Did you mean one of these?");
                Iterator<String> reactions = REACTIONS.keySet().iterator();
                for (String name : found) {
                    String reaction = reactions.next();
                    embedBuilder.addField(reaction + " " + name, "", true);
                }
                embedBuilder.setFooter(league.name() + " " + ivs, null);
                Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
                Message sent = rankBot.sendMessage(message).complete();
                // after sending, add reactions
                reactions = REACTIONS.values().iterator();
                for (String name : found) {
                    sent.addReaction(reactions.next()).submit();
                }
            }
            return;
        }

        RankService.rank(pokemon, league, ivs, rankBot);
    }

//        int levelFloor = pokemon.getLevelFloor();
//        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, league);
//        if (stats.isEmpty()) {
//            StatProduct statProduct = new StatProduct(pokemon, IndividualValues.ZERO, levelFloor);
//            rankBot.sendMessage(pokemonName + " is ineligible for " + league + " league. Min CP @ lvl " +
//                    levelFloor + " is " + statProduct.getCp()).submit();
//            return;
//        }
//
//        StatProduct statProduct = stats.get(ivs);
//        if (statProduct == null) {
//            StatProduct over = new StatProduct(pokemon, ivs, levelFloor);
//            rankBot.sendMessage("CP at lvl " + levelFloor + " for " + ivs + " is " + over.getCp() +
//                    " which is over " + league.maxCp + " for " + league + " league.").submit();
//            return;
//        }
//
//        Collection<StatProduct> statProducts = stats.values();
//        SortedSet<StatProduct> betterStats = statProducts.stream().
//                filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
//                collect(toCollection(TreeSet::new));
//        int rank = betterStats.size() + 1;
//        StatProduct wildStats = betterStats.isEmpty() ? statProduct : betterStats.first();
//        int bestStatProduct = wildStats.getStatProduct();
//
//        EmbedBuilder embedBuilder = new EmbedBuilder();
//        embedBuilder.setTitle(pokemon.getName());
//        User author = event.getAuthor();
//        embedBuilder.setFooter(author.getName(), author.getAvatarUrl());
//        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / wildStats.getStatProduct()) / 10.0;
//        String desc = "#" + rank + "/" + stats.size() + " | L" + statProduct.getLevel() + " | CP " +
//                statProduct.getCp() + " | " + percentBest + "%";
//        embedBuilder.addField(ivs.toString(), desc, false);
//
//        embedBuilder.addField("#1 Rank", getDesc(wildStats, bestStatProduct), false);
//
//        if (pokemon.isTradable() && league != League.master) {
//            statProducts.stream().
//                    filter(sp -> sp.getTradeLevel() == GREAT_FRIEND).
//                    sorted().findFirst().ifPresent(great ->
//                    embedBuilder.addField("Top Great Friend Trade:", getDesc(great, bestStatProduct), false));
//
//            statProducts.stream().
//                    filter(sp -> sp.getTradeLevel() == ULTRA_FRIEND).
//                    sorted().findFirst().ifPresent(ultra ->
//                    embedBuilder.addField("Top Ultra Friend Trade:", getDesc(ultra, bestStatProduct), false));
//
//            statProducts.stream().
//                    filter(sp -> sp.getTradeLevel() == BEST_FRIEND).
//                    sorted().findFirst().ifPresent(best ->
//                    embedBuilder.addField("Top Best Friend Trade:", getDesc(best, bestStatProduct), false));
//
//            statProducts.stream().
//                    filter(sp -> sp.getTradeLevel() == RAID_HATCH_RESEARCH).
//                    sorted().findFirst().ifPresent(raid ->
//                    embedBuilder.addField("Top Raid/Hatch/Research:", getDesc(raid, bestStatProduct), false));
//
//            statProducts.stream().
//                    filter(sp -> sp.getTradeLevel() == LUCKY_TRADE).
//                    sorted().findFirst().ifPresent(lucky ->
//                    embedBuilder.addField("Top Lucky Friend Trade:", getDesc(lucky, bestStatProduct), false));
//        }
//
//        if (pokemon.isTradable()) {
//            long count = betterStats.stream().filter(sp -> sp.getTradeLevel() == BEST_FRIEND).count();
//            double odds = Math.round(1000.0 * count / 1331) / 10.0;
//            embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
//        } else {
//            embedBuilder.setDescription("(not tradable)");
//        }

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

//        MessageBuilder builder = new MessageBuilder();
//        builder.setEmbed(embedBuilder.build());
//        Message message = builder.build();
//        rankBot.sendMessage(message).submit();
//    }
//
//    private static String percent(double d) {
//        return (Math.round(d * 10.0) / 10.0) + "%";
//    }
//
//    private static String getDesc(StatProduct statProduct, int bestStatProduct) {
//        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStatProduct) / 10.0;
//        return "L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
//                statProduct.getIvs().getAttack() + "/" + statProduct.getIvs().getDefense() + "/" +
//                statProduct.getIvs().getStamina() + " | " + percentBest + "%";
//    }

    @Override
    protected void sendHelp(MessageReceivedEvent event, Matcher matcher) {
        if (rankBot == null) {
            rankBot = event.getJDA().getTextChannelsByName("rank-bot", true).get(0);
        }

        rankBot.sendMessage("usage: $rank pokemonname-optionalforme atk def sta ultra|master (great league is " +
                "default)").submit();
        rankBot.sendMessage("example: $rank deoxys-defense 10 15 15").submit();
        rankBot.sendMessage("example: $rank giratina-altered 10 15 15 ultra").submit();
        rankBot.sendMessage("example: $rank giratina-origin 10 15 15 master").submit();
    }

}
