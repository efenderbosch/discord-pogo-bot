package net.fender.discord.listeners;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.QuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.fender.discord.filters.HasImageAttachmentFilter.HAS_IMAGE_ATTACHMENT_FILTER;
import static net.fender.discord.filters.MemberIsUserFilter.MEMBER_IS_USER_FILTER;

@Component
public class QuestScreenshotListener extends BaseEventListener<MessageReceivedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestScreenshotListener.class);

    static final String QUEST_CHANNEL_NAME = "quests";
    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            (QUEST_CHANNEL_NAME);

    private final QuestRepository questRepo;

    private TextChannel questChannel;

    @Autowired
    public QuestScreenshotListener(QuestRepository questRepo) {
        super(MessageReceivedEvent.class, MEMBER_IS_USER_FILTER, CHANNEL_NAME_FILTER, HAS_IMAGE_ATTACHMENT_FILTER);
        this.questRepo = questRepo;
    }

    @Override
    protected void processEvent(MessageReceivedEvent event) {
        if (questChannel == null) {
            JDA jda = event.getJDA();
            questChannel = jda.getTextChannelsByName(QUEST_CHANNEL_NAME, true).get(0);
        }
        Message message = event.getMessage();

        questChannel.sendTyping().submit();

        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        if (imageAttachments.size() > 1) {
            questChannel.sendMessage("I can't scan multiple images in the same post, yet.").submit();
            return;
        }

        SortedSet<Emote> emotes = new TreeSet<>(comparing(Emote::getName));
        JDA jda = message.getJDA();
        // TODO cache this?
        Map<String, Emote> emotesByName = jda.getEmotes().stream().collect(toMap(Emote::getName, identity()));
        questRepo.findAll().forEach(quest -> {
            String emoteName = quest.getEmote();
            Emote emote = emotesByName.get(emoteName);
            emotes.add(emote);
        });

        for (Emote emote : emotes) {
            message.addReaction(emote).submit();
        }
    }
}
