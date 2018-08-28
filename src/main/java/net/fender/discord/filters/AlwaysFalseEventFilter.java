package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

public enum AlwaysFalseEventFilter implements EventFilter<Event> {

    INSTANCE;

    @Override
    public boolean test(Event event) {
        return false;
    }
}
