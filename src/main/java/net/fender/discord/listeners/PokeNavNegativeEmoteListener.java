package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.fender.discord.filters.MemberIsBotFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class PokeNavNegativeEmoteListener extends BaseEventListener<MessageReactionAddEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PokeNavNegativeEmoteListener.class);

    @Autowired
    public PokeNavNegativeEmoteListener() {
        super(MessageReactionAddEvent.class, MemberIsBotFilter.INSTANCE);
    }

    @Override
    protected void processEvent(MessageReactionAddEvent event) {
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
        String reactionName = reactionEmote.getName();
        // unicode for :thumbsdown:
        if (!"\uD83D\uDC4E".equals(reactionName)) {
            return;
        }
        try {
            Message original = event.getTextChannel().getMessageById(event.getMessageId()).submit().get();
            User op = original.getMember().getUser();
            if (!op.isBot()) return;
            LOG.info("reaction {} by {} to {}", reactionName, op, original);
            original.delete().submit();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("exception getting original message", e);
        }
    }
}