package net.fender.discord.filters;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fender.discord.filters.ChannelTypeFilter.PRIVATE_CHANNEL_FILTER;
import static net.fender.discord.filters.ChannelTypeFilter.TEXT_CHANNEL_FILTER;

public class CommandParser implements Function<MessageReceivedEvent, Matcher> {

    private static final Matcher FAIL = Pattern.compile("").matcher("");

    private final Pattern pattern;

    public CommandParser(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Matcher apply(MessageReceivedEvent messageReceivedEvent) {
        if (!TEXT_CHANNEL_FILTER.test(messageReceivedEvent) &&
                !PRIVATE_CHANNEL_FILTER.test(messageReceivedEvent)) {
            return FAIL;
        }

        String content = messageReceivedEvent.getMessage().getContentRaw();
        return  pattern.matcher(content);
//        if (!matcher.matches()) return FAIL;
//        // group 0 is the entire match
//
//        int count = matcher.groupCount() + 1;
//        List<String> parts = new ArrayList<>(count);
//        for (int i = 0; i < count; i++) {
//            parts.add(matcher.group(i));
//        }
//        return parts;
    }
}
