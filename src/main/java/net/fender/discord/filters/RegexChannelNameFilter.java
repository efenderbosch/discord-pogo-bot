package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

import java.util.regex.Pattern;

public class RegexChannelNameFilter implements ChannelNameFilter {

    private final Pattern[] channelNamePatterns;

    public RegexChannelNameFilter(String... channelNamePatterns) {
        int length = channelNamePatterns.length;
        this.channelNamePatterns = new Pattern[length];
        for (int i = 0; i < length; i++) {
            this.channelNamePatterns[i] = Pattern.compile(channelNamePatterns[i]);
        }
    }

    @Override
    public boolean test(GenericMessageEvent event) {
        if (event == null) return false;

        MessageChannel channel = event.getChannel();
        if (channel == null) return false;

        String channelName = channel.getName();
        if (channelName == null) return false;

        for (Pattern channelNamePattern : channelNamePatterns) {
            if (channelNamePattern.matcher(channelName).matches()) {
                return true;
            }
        }
        return false;
    }
}
