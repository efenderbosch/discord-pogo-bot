package net.fender.discord.filters;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public enum AlwaysTrueGenericMessageEventFilter implements GenericMessageEventFilter {

    INSTANCE;

    @Override
    public boolean test(GenericMessageEvent genericMessageEvent) {
        return true;
    }
}
