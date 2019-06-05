package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toCollection;

@Component
public class RankListener extends CommandEventListener {

    private static final Pattern RANK = Pattern.compile("\\$rank\\s+(\\w+)\\s+([-\\w]+)\\s+(\\d{1,2})\\s+(\\d{1,2})" +
            "\\s+(\\d{1,2})");
    //private static final PokeApi POKE_API = new PokeApiClient();
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter("rank-bot");

    private final PokemonRegistry pokemonRegistry;
    private TextChannel rankBot;

    @Autowired
    public RankListener(PokemonRegistry pokemonRegistry) {
        super(RANK, CHANNEL_NAME_FILTER);
        this.pokemonRegistry = pokemonRegistry;
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        if (rankBot == null) {
            rankBot = event.getJDA().getTextChannelsByName("rank-bot", true).get(0);
        }
        rankBot.sendTyping().submit();

        String leagueName = parts.get(1);
        Optional<League> maybeLeague = League.find(leagueName);
        if (!maybeLeague.isPresent()) {
            rankBot.sendMessage("Unknown league " + leagueName + "!").submit();
            return;
        }

        League league = maybeLeague.get();

        String pokemonName = parts.get(2).toLowerCase();
        // reg ex won't pass if these aren't numbers, so should not need try/catch around conversion from String
        int atk = Integer.parseInt(parts.get(3));
        int def = Integer.parseInt(parts.get(4));
        int sta = Integer.parseInt(parts.get(5));
        IndividualValues ivs = new IndividualValues(atk, def, sta);

        Pokemon pokemon = pokemonRegistry.getPokeman(pokemonName);
        if (pokemon == null) {
            rankBot.sendMessage(pokemonName + " not found!").submit();
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
        String ivDesc = ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina();
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / wildStats.getStatProduct()) / 10.0;
        String desc = "#" + rank + "/" + stats.size() + " | L" + statProduct.getLevel() + " | CP " +
                statProduct.getCp() + " | " + percentBest + "%";
        embedBuilder.addField(ivDesc, desc, false);

        String numberOne = "#1 " + (pokemon.isTradable() ? " Wild:" : " Raid/Hatch:");

        embedBuilder.addField(numberOne , getDesc(wildStats, bestStatProduct), false);

        if (pokemon.isTradable()) {
            StatProduct goodStats = statProducts.stream().
                    filter(StatProduct::isGoodFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Good:", getDesc(goodStats, bestStatProduct), false);

            StatProduct greatStats = statProducts.stream().
                    filter(StatProduct::isGreatFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Great:", getDesc(greatStats, bestStatProduct), false);

            StatProduct ultraStats = statProducts.stream().
                    filter(StatProduct::isUltraFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Ultra:", getDesc(ultraStats, bestStatProduct), false);

            StatProduct bestStats = statProducts.stream().
                    filter(StatProduct::isBestFriend).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Best:", getDesc(bestStats, bestStatProduct), false);

            StatProduct raidHatchResearchStats = statProducts.stream().
                    filter(StatProduct::isRaidHatchResearch).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Raid:", getDesc(raidHatchResearchStats, bestStatProduct), false);

            StatProduct luckyStats = statProducts.stream().
                    filter(StatProduct::isLucky).
                    sorted().findFirst().get();
            embedBuilder.addField("#1 Lucky:", getDesc(luckyStats, bestStatProduct), false);

            long count = betterStats.stream().filter(StatProduct::isBestFriend).count();
            double odds = Math.round(1000.0 * count / 1331) / 10.0;
            embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);
        }

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
    }

    private String getDesc(StatProduct statProduct, int bestStatProduct) {
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStatProduct) / 10.0;
        return "L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
                statProduct.getIvs().getAttack() + "/" + statProduct.getIvs().getDefense() + "/" +
                statProduct.getIvs().getStamina() + " | " + percentBest + "%";
    }
}
