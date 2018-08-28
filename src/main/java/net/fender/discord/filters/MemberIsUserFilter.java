package net.fender.discord.filters;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public enum MemberIsUserFilter implements EventFilter<MessageReceivedEvent> {

    INSTANCE;

    @Override
    public boolean test(MessageReceivedEvent messageReceivedEvent) {
        return !messageReceivedEvent.getMessage().getMember().getUser().isBot();
    }
}
