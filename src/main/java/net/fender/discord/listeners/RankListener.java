package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.counting;

@Component
public class RankListener extends CommandEventWithHelpListener {

    private static final Pattern BASIC = Pattern.compile("\\$rank.*");
    private static final Pattern FULL = Pattern.compile("\\$rank\\s+([-\\w]+)\\s+(\\d{1,2})\\s+(\\d{1,2})" +
            "\\s+(\\d{1,2})\\s*(\\w*)");
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter("rank-bot");

    private final PokemonRegistry pokemonRegistry;
    private TextChannel rankBot;

    @Autowired
    public RankListener(PokemonRegistry pokemonRegistry) {
        super(BASIC, FULL, CHANNEL_NAME_FILTER);
        this.pokemonRegistry = pokemonRegistry;
    }

    @Override
    protected void doCommand(MessageReceivedEvent event, List<String> parts) {
        if (rankBot == null) {
            rankBot = event.getJDA().getTextChannelsByName("rank-bot", true).get(0);
        }
        rankBot.sendTyping().submit();

        League league = League.GREAT;
        String leagueName = parts.get(5);
        if (!leagueName.trim().isEmpty()) {
            Optional<League> maybeLeague = League.find(leagueName);
            if (!maybeLeague.isPresent()) {
                rankBot.sendMessage("Unknown league '" + leagueName + "'!").submit();
                sendHelp(event, parts);
                return;
            }
            league = maybeLeague.get();
        }

        String pokemonName = parts.get(1).toLowerCase();
        // reg ex won't pass if these aren't numbers, so should not need try/catch around conversion from String
        int atk = Integer.parseInt(parts.get(2));
        int def = Integer.parseInt(parts.get(3));
        int sta = Integer.parseInt(parts.get(4));
        IndividualValues ivs = new IndividualValues(atk, def, sta);

        Pokemon pokemon = pokemonRegistry.getPokeman(pokemonName);
        if (pokemon == null) {
            rankBot.sendMessage(pokemonName + " not found!").submit();
            sendHelp(event, parts);
            return;
        }

        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, league);
        StatProduct statProduct = stats.get(ivs);

        Collection<StatProduct> statProducts = stats.values();
        SortedSet<StatProduct> betterStats = statProducts.stream().
                filter(s -> s.getStatProduct() > statProduct.getStatProduct()).
                collect(toCollection(TreeSet::new));
        int rank = betterStats.size() + 1;
        StatProduct wildStats = betterStats.isEmpty() ? statProduct : betterStats.first();
        int bestStatProduct = wildStats.getStatProduct();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(pokemon.getName());
        User author = event.getAuthor();
        embedBuilder.setFooter(author.getName(), author.getAvatarUrl());
        String ivDesc = ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina();
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / wildStats.getStatProduct()) / 10.0;
        String desc = "#" + rank + "/" + stats.size() + " | L" + statProduct.getLevel() + " | CP " +
                statProduct.getCp() + " | " + percentBest + "%";
        embedBuilder.addField(ivDesc, desc, false);

        embedBuilder.addField("#1 Rank", getDesc(wildStats, bestStatProduct), false);

        if (pokemon.isTradable() && league != League.MASTER) {
            StatProduct greatStats = statProducts.stream().
                    filter(StatProduct::isGreatFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("Top Great Friend Trade:", getDesc(greatStats, bestStatProduct), false);

            StatProduct ultraStats = statProducts.stream().
                    filter(StatProduct::isUltraFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("Top Ultra Friend Trade:", getDesc(ultraStats, bestStatProduct), false);

            StatProduct bestStats = statProducts.stream().
                    filter(StatProduct::isBestFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("Top Best Friend Trade:", getDesc(bestStats, bestStatProduct), false);

            StatProduct raidHatchResearchStats = statProducts.stream().
                    filter(StatProduct::isRaidHatchResearch).
                    sorted().findFirst().get();
            embedBuilder.addField("Top Raid/Hatch/Research:", getDesc(raidHatchResearchStats, bestStatProduct), false);

            StatProduct luckyStats = statProducts.stream().
                    filter(StatProduct::isLucky).
                    sorted().findFirst().get();
            embedBuilder.addField("Top Lucky Friend Trade:", getDesc(luckyStats, bestStatProduct), false);
        }

        if (pokemon.isTradable()) {
            long count = betterStats.stream().filter(StatProduct::isBestFriend).count();
            double odds = Math.round(1000.0 * count / 1331) / 10.0;
            embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
        } else {
            embedBuilder.setDescription("(not tradable)");
        }

        int size = stats.size() / 8;
        List<StatProduct> top = stats.values().stream().sorted().limit(size).collect(toList());
        long amazes = top.stream().filter(StatProduct::isAmazes).count();
        embedBuilder.addField("Top Appraisal", percent(100.0 * amazes / size), true);
        long strong = top.stream().filter(StatProduct::isStrong).count();
        embedBuilder.addField("High Appraisal", percent(100.0 * strong / size), false);
        long decent = top.stream().filter(StatProduct::isDecent).count();
        embedBuilder.addField("Average Appraisal", percent(100.0 * decent / size), true);
        long notGreatInBattle = top.stream().filter(StatProduct::isNotGreatInBattle).count();
        embedBuilder.addField("Poor Appraisal", percent(100.0 * notGreatInBattle / size), false);
        long attackTop = top.stream().filter(StatProduct::isAttackBest).count();
        embedBuilder.addField("Attack Best Stat", percent(100.0 * attackTop / size), true);

        Map<Integer, Long> counts = top.stream().collect(groupingBy(StatProduct::getCp, counting()));
        long breakpoint = size / 16;
        long sum = 0;
        long cpBreakpoint = 1500;
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            int cp = entry.getKey();
            long count = entry.getValue();
            sum += count;
            if (sum >= breakpoint) {
                cpBreakpoint = cp;
                break;
            }
        }
        embedBuilder.addField("CP Eval Breakpoint", cpBreakpoint + "", false);

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
    }

    private static String percent(double d) {
        return (Math.round(d * 10.0) / 10.0) + "%";
    }

    private String getDesc(StatProduct statProduct, int bestStatProduct) {
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStatProduct) / 10.0;
        return "L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
                statProduct.getIvs().getAttack() + "/" + statProduct.getIvs().getDefense() + "/" +
                statProduct.getIvs().getStamina() + " | " + percentBest + "%";
    }

    @Override
    protected void sendHelp(MessageReceivedEvent event, List<String> parts) {
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
