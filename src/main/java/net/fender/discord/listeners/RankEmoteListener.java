package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.fender.pogo.IndividualValues;
import net.fender.pogo.League;
import net.fender.pogo.Pokemon;
import net.fender.pogo.PokemonRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class RankEmoteListener extends BaseEventListener<MessageReactionAddEvent> {

    private final PokemonRegistry pokemonRegistry;
    private TextChannel rankBot;
    static final Map<String, String> REACTIONS = new LinkedHashMap<>();

    @Autowired
    public RankEmoteListener(PokemonRegistry pokemonRegistry) {
        super(MessageReactionAddEvent.class, RankListener.CHANNEL_NAME_FILTER);
        this.pokemonRegistry = pokemonRegistry;
        REACTIONS.put("1⃣", "\u0031\u20E3");
        REACTIONS.put("2⃣", "\u0032\u20E3");
        REACTIONS.put("3⃣", "\u0033\u20E3");
        REACTIONS.put("4⃣", "\u0034\u20E3");
        REACTIONS.put("5⃣", "\u0035\u20E3");
        REACTIONS.put("6⃣", "\u0036\u20E3");
        REACTIONS.put("7⃣", "\u0037\u20E3");
        REACTIONS.put("8⃣", "\u0038\u20E3");
        REACTIONS.put("9⃣", "\u0039\u20E3");
    }

    @Override
    protected void processEvent(MessageReactionAddEvent event) {
        JDA jda = event.getJDA();
        if (rankBot == null) {
            rankBot = jda.getTextChannelsByName("rank-bot", true).get(0);
        }
        Message original = rankBot.getMessageById(event.getMessageId()).complete();
        User op = original.getMember().getUser();
        if (!op.isBot()) return;
        User reactingUser = event.getMember().getUser();
        if (reactingUser.isBot()) return;

        List<MessageEmbed> embeds = original.getEmbeds();
        if (embeds.isEmpty()) return;

        rankBot.sendTyping().submit();
        MessageEmbed embed = embeds.get(0);
        List<Field> fields = embed.getFields();
        String footer = embed.getFooter().getText();
        String[] parts = footer.split(" ");
        League league = League.valueOf(parts[0]);
        IndividualValues ivs = IndividualValues.parse(parts[1]);

        ReactionEmote reactionEmote = event.getReactionEmote();
        String reactionName = reactionEmote.getName();
        String pokemonName = fields.stream().
                filter(field -> field.getName().startsWith(reactionName)).
                findAny().
                get().
                getName().
                substring(2).
                trim();
        original.delete().submit();
        Pokemon pokemon = pokemonRegistry.getPokeman(pokemonName);
        RankService.rank(pokemon, league, ivs, rankBot);
    }
}
