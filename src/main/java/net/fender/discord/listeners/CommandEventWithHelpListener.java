package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.CommandParser;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommandEventWithHelpListener extends CommandEventListener {

    private final CommandParser fullParser;

    public CommandEventWithHelpListener(Pattern command, Pattern full, Predicate<? extends Event>... filters) {
        super(command, filters);
        fullParser = new CommandParser(full);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, Matcher matcher) {
        Matcher fullMatcher = fullParser.apply(event);
        if (!fullMatcher.matches()) {
            sendHelp(event, matcher);
        } else {
            doCommand(event, fullMatcher);
        }
    }

    protected abstract void doCommand(MessageReceivedEvent event, Matcher matcher);

    protected abstract void sendHelp(MessageReceivedEvent event, Matcher matcher);
}
