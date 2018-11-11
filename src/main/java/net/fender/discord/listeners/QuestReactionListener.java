package net.fender.discord.listeners;

import com.google.cloud.vision.v1.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.gce.metrics.VisionAPIRequestCounter;
import net.fender.gce.vision.ImageAnnotator;
import net.fender.pogo.Quest;
import net.fender.pogo.QuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.fender.discord.listeners.QuestScreenshotListener.QUEST_CHANNEL_NAME;

@Component
public class QuestReactionListener extends BaseEventListener<MessageReactionAddEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestReactionListener.class);

    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            (QUEST_CHANNEL_NAME);

    private final ImageAnnotator imageAnnotator;
    private final VisionAPIRequestCounter visionAPIRequestCounter;
    private final QuestRepository questRepo;

    private TextChannel questChannel;

    @Autowired
    public QuestReactionListener(ImageAnnotator imageAnnotator,
                                 VisionAPIRequestCounter visionAPIRequestCounter,
                                 QuestRepository questRepo) {
        super(MessageReactionAddEvent.class, CHANNEL_NAME_FILTER);
        this.imageAnnotator = imageAnnotator;
        this.visionAPIRequestCounter = visionAPIRequestCounter;
        this.questRepo = questRepo;
    }

    @Override
    protected void processEvent(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        JDA jda = event.getJDA();
        if (questChannel == null) {
            questChannel = jda.getTextChannelsByName(QUEST_CHANNEL_NAME, true).get(0);
        }
        questChannel.sendTyping().submit();

        String messageId = event.getMessageId();

        TextChannel textChannel = event.getTextChannel();
        Message message = textChannel.getMessageById(messageId).complete();
        User user = message.getMember().getUser();

        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        if (imageAttachments.size() != 1) {
            return;
        }

        Emote emote = event.getReactionEmote().getEmote();
        String emoteName = emote.getName();
        List<Quest> quests = questRepo.findByEmote(emoteName);
        if (quests.size() != 1) {
            // TODO
            LOG.info("WIP more than one quest w/ emote");
            textChannel.sendMessage("WIP more than one quest w/ emote.").submit();
            return;
        }
        Quest quest = quests.get(0);

        Attachment attachment = imageAttachments.get(0);
        boolean shouldDelete = processAttachment(user, quest, emote, attachment);

        if (shouldDelete) {
            message.delete().submit();
        }
    }

    private boolean processAttachment(User user, Quest quest, Emote emote, Attachment attachment) {
        long apiCount = visionAPIRequestCounter.count();
        if (apiCount > 957) {
            LOG.info("GCE Vision API usage: {}", apiCount);
            questChannel.sendMessage("Close to GCE Vision API Limit: " + apiCount).submit();
            return false;
        }

        try {
            int imageHeight = attachment.getHeight();
//            Graphics2D graphics2D = image.createGraphics();
//            graphics2D.setStroke(new BasicStroke(4));
            AnnotateImageResponse response = imageAnnotator.annotate(attachment);

            if (response.hasError()) {
                LOG.warn("GCE Vision error: {}", response.getError());
                questChannel.sendMessage("GCE Vision Error " + response.getError()).submit();
                return false;
            }

//            boolean isScreenshot = IS_SCREENSHOT.test(response);
//            boolean pokemonScreenshot = IS_POKEMON_SCREENSHOT.test(response);
//            boolean ivScreenshot = IS_IV_SCREENSHOT.test(response);

            StringBuilder temp = new StringBuilder();
            TextAnnotation textAnnotation = response.getFullTextAnnotation();
            for (Page page : textAnnotation.getPagesList()) {
                for (Block block : page.getBlocksList()) {
                    if (block.getBlockType() != Block.BlockType.TEXT) {
                        LOG.info("skipping block of type {}", block.getBlockType());
                        continue;
                    }
                    for (Paragraph paragraph : block.getParagraphsList()) {
                        int y = paragraph.getBoundingBox().getVertices(0).getY();
                        double percent = (1.0 * y / imageHeight);
                        if (percent < 0.05 || percent > 0.1) continue;

//                        drawBoundingPoly(graphics2D, Color.YELLOW, paragraph.getBoundingBox());

                        for (Word word : paragraph.getWordsList()) {
                            y = word.getBoundingBox().getVertices(0).getY();
                            percent = (1.0 * y / imageHeight);
                            if (percent < 0.05 || percent > 0.1) continue;

//                            drawBoundingPoly(graphics2D, Color.GREEN, word.getBoundingBox());
                            for (Symbol symbol : word.getSymbolsList()) {
                                temp.append(symbol.getText());
                            }
                            temp.append(' ');
                        }
                    }
                }
            }

            String pokestop = temp.toString().trim();
            if (pokestop.isEmpty()) {
                questChannel.sendMessage("Could not find a Pokestop name in that screenshot. Please send it to Fender.")
                        .submit();
                return false;
            }

            TextChannel botChat = questChannel.getJDA().getTextChannelsByName("bot-chat", true).get(0);
            Message pm = botChat.sendMessage("$si " + pokestop).complete();
            List<Message> history;
            while (true) {
                history = botChat.getHistoryAfter(pm.getId(), 1).complete().getRetrievedHistory();
                if (!history.isEmpty()) break;
                try {
                    LOG.info("sleeping, waiting for PokeNav to reply");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message reply = history.get(0);
            MessageEmbed messageEmbed = reply.getEmbeds().get(0);
            String title = messageEmbed.getTitle().trim();
            if ("Error".equalsIgnoreCase(title)) {
                questChannel.sendMessage("Pokenav could not find a Pokestop named '" +
                        pokestop + "'. Please send this screenshot to Fender.").submit();
                return false;
            }

            if ("Select Location".equalsIgnoreCase(title)) {
                questChannel.sendMessage("Pokenav found multiple Pokestops named '" + pokestop + "'. This is a WIP.")
                        .submit();
                LOG.info("title: {}", title);
                return false;
            }

            String url = messageEmbed.getUrl();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
            embedBuilder.setTitle(quest.getReward());
            embedBuilder.setDescription(quest.getQuest());
            embedBuilder.setThumbnail(emote.getImageUrl());
            embedBuilder.setImage(attachment.getUrl());
            embedBuilder.setDescription("[" + pokestop + "](" + url + ")");

            MessageBuilder builder = new MessageBuilder();
            builder.setEmbed(embedBuilder.build());
            questChannel.sendMessage(builder.build()).submit();
            return true;

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "png", baos);
//            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//            questChannel.sendFile(bais, "annotated-" + attachment.getFileName()).submit();

        } catch (IOException e) {
            LOG.error("exception processing attachment", e);
        }
        return false;
    }

    private void drawBoundingPoly(Graphics2D graphics2D, Color color, BoundingPoly boundingPoly) {
        graphics2D.setColor(color);
        Vertex topLeft = boundingPoly.getVertices(0);
        int x = topLeft.getX();
        int y = topLeft.getY();
        Vertex bottomRight = boundingPoly.getVertices(2);
        int width = bottomRight.getX() - x;
        int height = bottomRight.getY() - y;
        graphics2D.drawRect(x, y, width, height);
    }
}
