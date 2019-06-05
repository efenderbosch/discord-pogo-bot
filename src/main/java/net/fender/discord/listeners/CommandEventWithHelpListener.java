package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.CommandParser;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class CommandEventWithHelpListener extends CommandEventListener {

    private final CommandParser fullParser;

    public CommandEventWithHelpListener(Pattern command, Pattern full, Predicate<? extends Event>... filters) {
        super(command, filters);
        fullParser = new CommandParser(full);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        List<String> fullOptions = fullParser.apply(event);
        if (fullOptions.isEmpty()) {
            sendHelp(event, parts);
        } else {
            doCommand(event, fullOptions);
        }
    }

    protected abstract void doCommand(MessageReceivedEvent event, List<String> parts);

    protected abstract void sendHelp(MessageReceivedEvent event, List<String> parts);
}
