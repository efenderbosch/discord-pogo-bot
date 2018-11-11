package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

import java.util.function.Predicate;

public enum AlwaysTrueEventFilter implements Predicate<Event> {

    ALWAYS_TRUE_EVENT_FILTER;

    @Override
    public boolean test(Event event) {
        return true;
    }
}
