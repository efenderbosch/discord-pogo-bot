package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static net.fender.discord.filters.MemberIsUserFilter.MEMBER_IS_USER_FILTER;

@Component
public class BlessedListener extends CommandEventListener {

    public static final Pattern BLESSSED = Pattern.compile(".*(bles+ed).*", CASE_INSENSITIVE);

    public BlessedListener() {
        super(BLESSSED, MEMBER_IS_USER_FILTER);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        Message message = event.getMessage();
        JDA jda = message.getJDA();
        Emote emote = jda.getEmotesByName("penta", true).get(0);
        message.addReaction(emote).submit();
    }
}
