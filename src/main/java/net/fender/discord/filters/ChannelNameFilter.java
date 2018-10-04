package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class ChannelNameFilter implements EventFilter<GenericMessageEvent> {

    private final Set<String> channelNames;

    public ChannelNameFilter(String... channelNames) {
        this.channelNames = Arrays.stream(channelNames).collect(toSet());
    }

    @Override
    public boolean test(GenericMessageEvent event) {
        if (event == null) return false;

        MessageChannel channel = event.getChannel();
        if (channel == null) return false;

        String channelName = channel.getName();
        if (channelName == null) return false;

        return channelNames.contains(channelName);
    }
}
