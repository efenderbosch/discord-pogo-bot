package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

import java.time.LocalTime;
import java.util.function.Predicate;

public class TimeFilter implements Predicate<Event> {

    private final LocalTime start;
    private final LocalTime end;

    public TimeFilter(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean test(Event event) {
        LocalTime now = LocalTime.now();
        return start.isBefore(now) && end.isAfter(now);
    }
}
