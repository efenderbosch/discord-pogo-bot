package net.fender.discord.listeners;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.MemberIsUserFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
public class TeamRocketTextListener extends CommandEventListener {

    static final Pattern TEAM_ROCKET = Pattern.compile(".*(team rocket|giovanni|jessie|james|Prepare for trouble|Make" +
                    " it double|To protect the world from devastation|To unite all peoples within our nation|To " +
                    "denounce the evils of truth and love|To extend our reach to the stars above|blast off at the " +
                    "speed of light|Surrender now, or prepare to fight|Meowth! That's right).*",
            CASE_INSENSITIVE);

    private static final String[] EMOTES = {"team_rocket", "jessie", "james", "giovanni", "rainbow_rocket"};
    private static final Random RANDOM = new Random();

    public TeamRocketTextListener() {
        super(TEAM_ROCKET, MemberIsUserFilter.MEMBER_IS_USER_FILTER);
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        Message message = event.getMessage();
        String emoteName = EMOTES[RANDOM.nextInt(EMOTES.length)];
        Emote teamRocket = message.getJDA().getEmotesByName(emoteName, true).get(0);
        message.addReaction(teamRocket).submit();
        // https://open.spotify.com/track/6rPO02ozF3bM7NnOV4h6s2?si=Bqp-3oMVSd6gLiRBCmW4vw
    }
}
