package net.fender.discord.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liqp.Template;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.fender.discord.filters.ChannelNameFilter;
import net.fender.pogo.Quest;
import net.fender.pogo.QuestRepository;
import net.fender.pogo.Report;
import net.fender.pogo.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static net.fender.discord.listeners.QuestScreenshotListener.QUEST_CHANNEL_NAME;
import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PUBLIC_READ;

@Component
public class QuestReactionListener extends BaseEventListener<MessageReactionAddEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(QuestReactionListener.class);

    private static final ChannelNameFilter CHANNEL_NAME_FILTER = new ChannelNameFilter
            (QUEST_CHANNEL_NAME);

    private final RekognitionClient rekognition;
    private final QuestRepository questRepo;
    private final ReportRepository reportRepo;
    private final ObjectMapper objectMapper;
    private final S3Client s3;
    private final Template template;

    private TextChannel questChannel;

    @Autowired
    public QuestReactionListener(RekognitionClient rekognition,
                                 QuestRepository questRepo,
                                 ReportRepository reportRepo,
                                 ObjectMapper objectMapper,
                                 S3Client s3,
                                 ResourceLoader resourceLoader) throws IOException {
        super(MessageReactionAddEvent.class, CHANNEL_NAME_FILTER);
        this.rekognition = rekognition;
        this.questRepo = questRepo;
        this.reportRepo = reportRepo;
        this.objectMapper = objectMapper;
        this.s3 = s3;
        Resource resource = resourceLoader.getResource("classpath:geojson.liquid");
        InputStream is = resource.getInputStream();
        template = Template.parse(IoUtils.toUtf8String(is));
    }

    @Override
    protected void processEvent(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        JDA jda = event.getJDA();
        if (questChannel == null) {
            questChannel = jda.getTextChannelsByName(QUEST_CHANNEL_NAME, true).get(0);
        }

        String messageId = event.getMessageId();
        TextChannel textChannel = event.getTextChannel();
        Message message = textChannel.getMessageById(messageId).complete();
        List<Attachment> imageAttachments = message.getAttachments().stream().
                filter(Attachment::isImage).
                collect(toList());

        if (imageAttachments.size() != 1) {
            return;
        }

        questChannel.sendTyping().submit();

        User user = message.getMember().getUser();
        String emoteName = event.getReactionEmote().getName();

        List<MessageReaction> reactions = message.getReactions();
        if ("❌".equals(emoteName)) {
            reactions.stream().
                    filter(MessageReaction::isSelf).
                    forEach(reaction -> reaction.removeReaction().submit());
            event.getReaction().removeReaction().submit();
            return;
        }

        List<Quest> quests = questRepo.findByEmote(emoteName);
        if (quests.size() != 1) {
            // TODO
            LOG.info("WIP more than one quest w/ emote");
            textChannel.sendMessage("WIP more than one quest w/ emote.").submit();
            return;
        }
        Quest quest = quests.get(0);

        if (!event.getReactionEmote().isEmote()) return;

        Emote emote = event.getReactionEmote().getEmote();
        Attachment attachment = imageAttachments.get(0);
        boolean shouldDelete = processAttachment(user, quest, emote, attachment);

        if (shouldDelete) {
            message.delete().submit();
        } else {
            reactions.stream().
                    filter(MessageReaction::isSelf).
                    forEach(reaction -> reaction.removeReaction().submit());
        }
    }

    private boolean processAttachment(User user, Quest quest, Emote emote, Attachment attachment) {
        SdkBytes sdkBytes;
        try {
            sdkBytes = SdkBytes.fromInputStream(attachment.getInputStream());
        } catch (IOException e) {
            LOG.error("exception getting attachment", e);
            questChannel.sendMessage("exception getting attachment " + e.getMessage()).submit();
            return false;
        }

        Image image = Image.builder().bytes(sdkBytes).build();
        DetectTextRequest request = DetectTextRequest.builder().image(image).build();
        DetectTextResponse response = rekognition.detectText(request);

        StringBuilder stringBuilder = new StringBuilder();
        for (TextDetection textDetection : response.textDetections()) {
            if (textDetection.type() != TextTypes.LINE) continue;

            float top = textDetection.geometry().boundingBox().top();
            if (top < 0.05 || top > 0.09) continue;

            stringBuilder.append(textDetection.detectedText()).append(' ');
        }

        String pokestop = stringBuilder.toString().trim();
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
        Field coordinates = messageEmbed.getFields().stream().
                filter(field -> field.getName().equals("coordinates")).findAny().get();
        String[] parts = coordinates.getValue().split(",");
        double latitude = Double.parseDouble(parts[0].trim());
        double longitude = Double.parseDouble(parts[1].trim());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
        embedBuilder.setTitle(quest.getReward() + "\n" + quest.getQuest());
        embedBuilder.setThumbnail(emote.getImageUrl());
        embedBuilder.setImage(attachment.getUrl());
        embedBuilder.setDescription("[" + pokestop + "](" + url + ")");
        embedBuilder.setTimestamp(ZonedDateTime.now());

        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        questChannel.sendMessage(builder.build()).submit();

        Report report = new Report();
        report.setPokestop(pokestop);
        report.setTask(quest.getQuest());
        report.setReward(quest.getReward());
        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setReportedAt(LocalDate.now());
        reportRepo.save(report);

        List<Report> reports = (List<Report>) reportRepo.findAll();
        try {
            ArrayNode reportsNode = objectMapper.convertValue(reports, ArrayNode.class);
            ObjectNode root = objectMapper.createObjectNode();
            root.putArray("reports").addAll(reportsNode);
            String input = root.toString();
            String json = template.render(input);
            LOG.info("geojson: {}", json);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder().
                    acl(PUBLIC_READ).
                    bucket("viridian-pokemongo-map").
                    key("pokestops.js").
                    build();
            RequestBody requestBody = RequestBody.fromString(json);
            PutObjectResponse putObjectResponse = s3.putObject(putObjectRequest, requestBody);
            LOG.info("s3 putObjectResponse isSuccessful: {}", putObjectResponse.sdkHttpResponse().isSuccessful());
        } catch (SdkException e) {
            LOG.error("exception doing template stuff", e);
        }

        return true;
    }
}
