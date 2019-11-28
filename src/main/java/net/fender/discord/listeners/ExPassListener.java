package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class ExPassListener extends CommandEventListener {

    private static final Pattern EX_PASS = Pattern.compile("React to this message if you have or want a pass to.*",
            CASE_INSENSITIVE);
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            ("ex-pass-coordination");

    public ExPassListener(Predicate<? extends Event>... filters) {
        super(EX_PASS, filters);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, Matcher matcher) {
        Message message = event.getMessage();
        // :thumbsup:
        message.addReaction("\uD83D\uDC4D").submit();
        // :v:
        message.addReaction("✌").submit();
        // :raised_hand:
        message.addReaction("\u270B").submit();
        // :ok_hand:
        message.addReaction("\uD83D\uDC4C").submit();
    }
}
