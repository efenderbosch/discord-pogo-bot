package net.fender.discord.listeners;

import me.sargunvohra.lib.pokekotlin.client.ErrorResponse;
import me.sargunvohra.lib.pokekotlin.client.PokeApi;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.StatProduct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@Component
public class RankListener extends CommandEventListener {

    private static final Pattern RANK = Pattern.compile("\\$rank\\s+(\\w+)\\s+([-\\w]+)\\s+(\\d{1,2})\\s+(\\d{1,2})" +
            "\\s+(\\d{1,2})");
    private static final PokeApi POKE_API = new PokeApiClient();
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter("rank-bot");

    private TextChannel rankBot;

    public RankListener() {
        super(RANK, CHANNEL_NAME_FILTER);
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

        String pokemonName = parts.get(2);
        // reg ex won't pass if these aren't numbers, so should not need try/catch around conversion from String
        int atk = Integer.parseInt(parts.get(3));
        int def = Integer.parseInt(parts.get(4));
        int sta = Integer.parseInt(parts.get(5));
        IndividualValues ivs = new IndividualValues(atk, def, sta);

        Pokemon pokemon = null;
        try {
            pokemon = POKE_API.getPokemon(pokemonName);
        } catch (Throwable e) {
            if (e instanceof ErrorResponse) {
                ErrorResponse er = (ErrorResponse) e;
                if (er.getCode() == 404) {
                    rankBot.sendMessage(pokemonName + " not found!").submit();
                    return;
                }
            }
        }

        Map<IndividualValues, StatProduct> stats = StatProduct.generateStatProducts(pokemon, League.GREAT);
        StatProduct statProduct = stats.get(ivs);

        SortedSet<StatProduct> betterStats = stats.values().stream().
                filter(s -> s.getStatProduct() >= statProduct.getStatProduct()).
                collect(toCollection(TreeSet::new));
        int rank = betterStats.size();
        StatProduct bestStats = betterStats.first();

        List<StatProduct> bestFriends = betterStats.stream().filter(StatProduct::isBestFriend).collect(toList());
        double odds = Math.round(1000.0 * bestFriends.size() / 1331) / 10.0;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(pokemon.getSprites().getFrontDefault());
        embedBuilder.setTitle(pokemon.getName());
        String ivDesc = ivs.getAttack() + "/" + ivs.getDefense() + "/" + ivs.getStamina();
        double percentBest = Math.round(1000.0 * statProduct.getStatProduct() / bestStats.getStatProduct()) / 10.0;
        String desc = "#" + rank + " | L" + statProduct.getLevel() + " | CP " + statProduct.getCp() + " | " +
                percentBest + "%";
        embedBuilder.addField(ivDesc, desc, false);

        String bestDesc = "L" + bestStats.getLevel() + " | CP " + bestStats.getCp() + " | " +
                bestStats.getIvs().getAttack() + "/" + bestStats.getIvs().getDefense() + "/" +
                bestStats.getIvs().getStamina();
        embedBuilder.addField("#1", bestDesc, false);
        embedBuilder.addField("Odds Best Friend Trade Will Improve Rank", odds + "%", false);

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        Message message = builder.build();
        rankBot.sendMessage(message).submit();
    }
}
