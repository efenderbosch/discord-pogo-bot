package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

public class EventIsInstanceFilter<T extends Event> implements EventFilter<T> {

    private final Class<Event> clazz;

    public EventIsInstanceFilter(Class<Event> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean test(Event event) {
        return clazz.isInstance(event);
    }

}
