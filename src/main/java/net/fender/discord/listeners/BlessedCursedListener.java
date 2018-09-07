package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.MemberIsUserFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class BlessedCursedListener extends CommandEventListener {

    public static final Pattern BLESSSED = Pattern.compile(".*(blessed).*", CASE_INSENSITIVE);

    public BlessedCursedListener() {
        super(BLESSSED, MemberIsUserFilter.INSTANCE);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        Message message = event.getMessage();
        JDA jda = message.getJDA();
        List<Emote> emotes = jda.getEmotesByName("penta", true);
        message.addReaction(emotes.get(0)).submit();
    }
}
