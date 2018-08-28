package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.EventFilter;

public class ResearchScreenshotListener extends BaseEventListener<MessageReceivedEvent> {

    public ResearchScreenshotListener(EventFilter<? extends Event>... filters) {
        super(MessageReceivedEvent.class, filters);
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {

    }
}
