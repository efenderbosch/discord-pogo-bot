package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

import java.util.EnumSet;

import static net.dv8tion.jda.core.entities.ChannelType.PRIVATE;
import static net.dv8tion.jda.core.entities.ChannelType.TEXT;

public class ChannelTypeFilter implements EventFilter<GenericMessageEvent> {

    public static final EnumSet<ChannelType> ANY = EnumSet.allOf(ChannelType.class);
    public static final ChannelTypeFilter TEXT_CHANNEL_FILTER = new ChannelTypeFilter(TEXT);
    public static final ChannelTypeFilter PRIVATE_CHANNEL_FILTER = new ChannelTypeFilter(PRIVATE);

    private final EnumSet<ChannelType> channelTypes;

    public ChannelTypeFilter(ChannelType channelType) {
        this.channelTypes = EnumSet.of(channelType);
    }

    public ChannelTypeFilter(EnumSet<ChannelType> channelTypes) {
        this.channelTypes = channelTypes;
    }

    @Override
    public boolean test(GenericMessageEvent event) {
        if (event == null) return false;

        ChannelType channelType = event.getChannelType();
        if (channelType == null) return false;

        return channelTypes.contains(channelType);
    }
}
