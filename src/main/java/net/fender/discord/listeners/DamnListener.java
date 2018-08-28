package net.fender.discord.listeners;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.MemberIsUserFilter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

//@Component
public class DamnListener extends CommandEventListener {

    // TODO count muliple instances of damn in the same message
    private static final Pattern DAMN = Pattern.compile(".*(damn|dammit\\w*)*.*", CASE_INSENSITIVE);
    private final AtomicInteger count = new AtomicInteger();

    public DamnListener() {
        super(DAMN, MemberIsUserFilter.INSTANCE);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        Message countMessage =  new MessageBuilder().append("damn count: ").append(count.incrementAndGet()).build();
        event.getTextChannel().sendMessage(countMessage).submit();
    }
}
