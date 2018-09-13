package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class BlessedCursedListener extends CommandEventListener {

    public static final Pattern BLESSSED = Pattern.compile(".*(blessed|jessie|james|Prepare for trouble|Make it " +
                    "double|To protect the world from devastation|To unite all peoples within our nation).*",
            CASE_INSENSITIVE);

    public BlessedCursedListener() {
        super(BLESSSED);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        Message message = event.getMessage();
        Emote teamRocket = message.getJDA().getEmotesByName("team_rocket", true).get(0);
        message.addReaction(teamRocket).submit();
    }
}
