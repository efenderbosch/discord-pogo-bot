package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.Quest;
import net.fender.pogo.QuestRepository;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static net.fender.discord.listeners.QuestScreenshotListener.QUEST_CHANNEL_NAME;

@Component
public class QuestCommandListener extends CommandEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(QuestCommandListener.class);

    private static final Pattern QUEST = Pattern.compile("\\$quest\\s+(\\w+)\\s*(.*)");
    private static final ChannelNameFilter QUEST_ADMIN = new ChannelNameFilter("quest-admin");

    private final QuestRepository questRepo;

    private TextChannel questChannel;

    @Autowired
    public QuestCommandListener(QuestRepository questRepo) {
        super(QUEST, QUEST_ADMIN);
        this.questRepo = questRepo;
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        TextChannel textChannel = event.getTextChannel();

        String command = parts.get(1);
        String[] options = parts.get(2).split(",");
        if (options.length == 1 && command.equalsIgnoreCase("list")) {
            listAll(textChannel);
        } else if (options.length == 1 && command.equalsIgnoreCase("help")) {
            sendHelp(textChannel);
        } else if (options.length == 3 && command.equalsIgnoreCase("add")) {
            addQuest(textChannel, options);
        } else if (options.length == 1 && command.equalsIgnoreCase("delete")) {
            deleteQuest(textChannel, options);
        } else if (options.length == 1 && command.equalsIgnoreCase("clear")) {
            clearReportedQuests(textChannel);
        } else {
            textChannel.sendMessage("unknown quest command " + command + " " + options).submit();
        }
    }

    private void sendHelp(TextChannel textChannel) {
        textChannel.sendMessage("quest commands:\n" +
                "'$quest add :emote:, task, reward' to add a new quest\n" +
                "'$quest list' to show all quests\n" +
                "'$quest delete quest#' to delete a quest\n" +
                "'$quest clear' to clear reports after a quest rotation").submit();
    }

    private void listAll(TextChannel textChannel) {
        LOG.info("listing quests");

        Iterable<Quest> quests = questRepo.findAll();
        List<Quest> sortedQuests = StreamSupport.stream(quests.spliterator(), false).
                sorted().
                collect(Collectors.toList());

        if (sortedQuests.isEmpty()) {
            textChannel.sendMessage("no quests").submit();
        }

        textChannel.sendMessage("listing " + sortedQuests.size() + " quests").submit();
        JDA jda = textChannel.getJDA();
        Map<String, Emote> emotes = jda.getEmotes().stream().collect(toMap(Emote::getName, identity()));

        RequestFuture<Message> lastEmbed = null;
        for (Quest quest : sortedQuests) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor("quest #" + quest.getId());
            embedBuilder.setTitle(quest.getReward());
            embedBuilder.setDescription(quest.getQuest());
            Emote emote = emotes.get(quest.getEmote());
            if (emote != null) {
                embedBuilder.setThumbnail(emote.getImageUrl());
            }
            Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
            lastEmbed = textChannel.sendMessage(message).submit();
        }

        try {
            lastEmbed.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("exception waiting for quest embed to post", e);
        }
        textChannel.sendMessage("end of quest list").submit();
    }

    private void addQuest(TextChannel textChannel, String[] options) {
        JDA jda = textChannel.getJDA();

        Quest quest = new Quest();
        String emoteName = options[0].split(":")[1];
        Emote emote = jda.getEmotesByName(emoteName, true).get(0);
        quest.setEmote(emote.getName());
        quest.setQuest(options[1].trim());
        quest.setReward(options[2].trim());
        quest = questRepo.save(quest);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("quest #" + quest.getId());
        embedBuilder.setTitle(quest.getReward());
        embedBuilder.setDescription(quest.getQuest());
        embedBuilder.setThumbnail(emote.getImageUrl());
        embedBuilder.setFooter("added", null);
        embedBuilder.setTimestamp(ZonedDateTime.now());

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
        textChannel.sendMessage(message).submit();
    }

    private void deleteQuest(TextChannel textChannel, String[] options) {
        Long id;
        try {
            id = Long.valueOf(options[0]);
        } catch (NumberFormatException e) {
            textChannel.sendMessage("usage: $quest delete <quest #>. " + options[0] + " is not a number.").submit();
            return;
        }

        Optional<Quest> maybeQuest = questRepo.findById(id);
        if (!maybeQuest.isPresent()) {
            textChannel.sendMessage("quest #" + id + " not found").submit();
            return;
        }

        JDA jda = textChannel.getJDA();

        Quest quest = maybeQuest.get();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("quest #" + quest.getId());
        embedBuilder.setTitle(quest.getReward());
        embedBuilder.setDescription(quest.getQuest());
        Emote emote = jda.getEmotesByName(quest.getEmote(), true).get(0);
        embedBuilder.setThumbnail(emote.getImageUrl());
        embedBuilder.setFooter("deleted", null);
        embedBuilder.setTimestamp(ZonedDateTime.now());

        questRepo.delete(quest);

        Message message = new MessageBuilder().setEmbed(embedBuilder.build()).build();
        textChannel.sendMessage(message).submit();
    }

    private void clearReportedQuests(TextChannel adminChannel) {
        if (questChannel == null) {
            questChannel = adminChannel.getJDA().getTextChannelsByName(QUEST_CHANNEL_NAME, true).get(0);
        }
        adminChannel.sendTyping().submit();
        MutableInt count = new MutableInt();
        questChannel.getIterableHistory().stream().
                filter(message -> message.getMember().getUser().isBot()).
                peek(message -> count.increment()).
                forEach(message -> message.delete().submit());
        adminChannel.sendMessage("Deleted " + count.getValue() + " reports.").submit();
        questChannel.sendMessage("Deleted quest reports due to rotation.").submit();
        LOG.info("purged {} reports", count.getValue());
    }

}
