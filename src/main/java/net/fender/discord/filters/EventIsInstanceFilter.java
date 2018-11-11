package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

import java.util.function.Predicate;

public class EventIsInstanceFilter<T extends Event> implements Predicate<T> {

    private final Class<Event> clazz;

    public EventIsInstanceFilter(Class<Event> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean test(Event event) {
        return clazz.isInstance(event);
    }

}
