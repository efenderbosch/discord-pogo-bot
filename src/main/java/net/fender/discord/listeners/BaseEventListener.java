package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import net.fender.discord.filters.EventFilter;
import net.fender.discord.filters.EventIsInstanceFilter;

public abstract class BaseEventListener<T extends Event> implements EventListener {

    private static final EventFilter[] EMPTY = new EventFilter[0];

    private final EventIsInstanceFilter classFilter;
    private final EventFilter<?>[] filters;

    protected BaseEventListener(Class<T> clazz, EventFilter<? extends Event>... filters) {
        classFilter = new EventIsInstanceFilter(clazz);
        this.filters = filters == null ? EMPTY : filters;
    }

    @Override
    public void onEvent(Event event) {
        if (!classFilter.test(event)) return;

        T typedEvent = (T) event;
        for (EventFilter filter : filters) {
            if (!filter.test(typedEvent)) return;
        }
        processEvent(typedEvent);
    }

    protected abstract void processEvent(T event);
}
