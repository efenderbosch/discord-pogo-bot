package net.fender.discord.filters;

import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public enum AlwaysFalseGenericMessageEventFilter implements GenericMessageEventFilter {

    INSTANCE;

    @Override
    public boolean test(GenericMessageEvent genericMessageEvent) {
        return false;
    }
}
