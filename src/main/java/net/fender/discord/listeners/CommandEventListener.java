package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.CommandParser;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class CommandEventListener extends BaseEventListener<MessageReceivedEvent> {

    private final CommandParser commandParser;

    protected CommandEventListener(Pattern command, Predicate<? extends Event>... filters) {
        super(MessageReceivedEvent.class, filters);
        commandParser = new CommandParser(command);
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {
        List<String> parts = commandParser.apply(event);
        if (parts.isEmpty()) return;
        processCommand(event, parts);
    }

    protected abstract void processCommand(MessageReceivedEvent event, List<String> parts);
}
