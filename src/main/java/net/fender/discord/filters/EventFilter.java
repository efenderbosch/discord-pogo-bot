package net.fender.discord.filters;

import net.dv8tion.jda.core.events.Event;

import java.util.function.Predicate;

@FunctionalInterface
public interface EventFilter<T extends Event> extends Predicate<T> {}
