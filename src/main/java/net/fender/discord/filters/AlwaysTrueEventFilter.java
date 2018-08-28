package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

public enum AlwaysTrueEventFilter implements EventFilter<Event> {

    INSTANCE;

    @Override
    public boolean test(Event event) {
        return true;
    }
}
