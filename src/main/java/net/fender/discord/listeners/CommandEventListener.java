package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;

import static net.fender.discord.filters.ChannelTypeFilter.TEXT_CHANNEL_FILTER;

public abstract class CommandEventListener extends ListenerAdapter {

    private final String command;

    protected CommandEventListener(String command) {
        this.command = command;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!TEXT_CHANNEL_FILTER.test(event)) return;
        String contentRaw = event.getMessage().getContentRaw();
        if (StringUtils.startsWith(contentRaw, command)) {
            processCommand(event);
        }
    }

    protected abstract void processCommand(MessageReceivedEvent event);
}
