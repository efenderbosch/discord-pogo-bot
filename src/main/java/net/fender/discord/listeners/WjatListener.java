package net.fender.discord.listeners;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.RegexChannelNameFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class WjatListener extends CommandEventListener {

    private static final Pattern WHAT = Pattern.compile(".*(what\\w*).*", CASE_INSENSITIVE);
    private static final RegexChannelNameFilter AFTER_HOURS =new RegexChannelNameFilter("after-hours");

    public WjatListener() {
        super(WHAT, AFTER_HOURS);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        String wjat = parts.get(1).replaceFirst("h", "j").replaceFirst("H", "J");
        event.getTextChannel().sendMessage(wjat + " ¯\\_(ツ)_/¯").submit();
    }
}
