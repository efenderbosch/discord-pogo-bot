package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import net.fender.discord.filters.EventIsInstanceFilter;

import java.util.function.Predicate;

public abstract class BaseEventListener<T extends Event> implements EventListener {

    private static final Predicate[] EMPTY = new Predicate[0];

    private final EventIsInstanceFilter classFilter;
    private final Predicate<? extends Event>[] filters;

    protected BaseEventListener(Class<T> clazz, Predicate<? extends Event>... filters) {
        classFilter = new EventIsInstanceFilter(clazz);
        this.filters = filters == null ? EMPTY : filters;
    }

    @Override
    public void onEvent(Event event) {
        if (!classFilter.test(event)) return;

        T typedEvent = (T) event;
        for (Predicate filter : filters) {
            if (!filter.test(typedEvent)) return;
        }
        processEvent(typedEvent);
    }

    protected abstract void processEvent(T event);
}
