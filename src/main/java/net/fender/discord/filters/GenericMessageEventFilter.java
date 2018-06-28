package net.fender.discord.filters;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

import java.util.function.Predicate;

@FunctionalInterface
public interface GenericMessageEventFilter extends Predicate<GenericMessageEvent> {}
