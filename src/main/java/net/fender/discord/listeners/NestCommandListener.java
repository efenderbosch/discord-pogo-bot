package net.fender.discord.listeners;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.fender.pogo.Nest;
import net.fender.pogo.NestRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;

@Component
public class NestCommandListener extends CommandEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NestCommandListener.class);

    private static final Pattern NEST = Pattern.compile("\\$nest\\s+(\\w+)\\s*(.*)");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd hh:mm a");

    private final NestRepository nestRepo;

    @Autowired
    public NestCommandListener(NestRepository nestRepo) {
        super(NEST);
        this.nestRepo = nestRepo;
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        PrivateChannel privateChannel = event.getPrivateChannel();
        if (privateChannel == null) {
            privateChannel = event.getMember().getUser().openPrivateChannel().complete();
        }

        String memberName = privateChannel.getUser().getName();

        String command = parts.get(1);
        String[] options = parts.get(2).split(",");
        if (options.length == 1 && command.equalsIgnoreCase("list")) {
            listAll(privateChannel);
        } else if (options.length == 1 && command.equalsIgnoreCase("help")) {
            sendHelp(privateChannel);
        } else if (options.length == 3 && command.equalsIgnoreCase("add")) {
            addNest(privateChannel, memberName, options);
        } else if (options.length == 1 && command.equalsIgnoreCase("delete")) {
            deleteNest(privateChannel, options);
        } else if (options.length == 2 && command.equalsIgnoreCase("update")) {
            updateNest(privateChannel, memberName, options);
        } else if (options.length == 1 && command.equalsIgnoreCase("clear")) {
            clearNests(privateChannel, memberName);
        } else {
            privateChannel.sendMessage("unknown $nest command").submit();
        }
    }

    private void sendHelp(PrivateChannel privateChannel) {
        privateChannel.sendMessage("nest commands:\n" +
                "'$nest add name, longitude, latitude' to add a new nest\n" +
                "'$nest update name, pokemon' to update a nest to a new pokemon\n" +
                "'$nest list' to show all nests\n" +
                "'$nest clear' to clear all nests after rotation").submit();
    }

    private void listAll(PrivateChannel privateChannel) {
        LOG.info("listing nests");

        Message message = buildAllNestMessage();
        privateChannel.sendMessage(message).submit();
    }

    private Message buildAllNestMessage() {
        Iterable<Nest> nests = nestRepo.findAll();
        List<Nest> sortedNests = StreamSupport.stream(nests.spliterator(), false).
                sorted().
                collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTimestamp(ZonedDateTime.now());

        List<String> stale = new ArrayList<>();
        for (Nest nest : sortedNests) {
            String nestName = nest.getName();
            if (nest.getPokemon() == null || nest.getReportedAt().isBefore(ZonedDateTime.now().minusDays(14))) {
                stale.add(nestName);
                continue;
            }

            String pokemon = StringUtils.abbreviate(nest.getPokemon() == null ? "unknown" : nest.getPokemon(), 20);
            String reportedBy = StringUtils.abbreviate(nest.getReportedBy(), 20);
            String reportedAt = FORMATTER.format(nest.getReportedAt());
            String value = pokemon + " " + reportedBy + " " + reportedAt;
            embedBuilder.addField(nestName, value, false);
        }

        if (!stale.isEmpty()) {
            embedBuilder.addField("Stale Nests:", stale.stream().collect(joining(", ")), false);
        }
        MessageBuilder builder = new MessageBuilder();
        builder.setEmbed(embedBuilder.build());
        return builder.build();
    }

    private void addNest(PrivateChannel channel, String memberName, String[] options) {
        Nest nest = new Nest();
        nest.setName(options[0].trim());
        nest.setLongitude(Double.parseDouble(options[1]));
        nest.setLatitude(Double.parseDouble(options[2]));
        nest.setReportedBy(memberName);
        nest.setReportedAt(ZonedDateTime.now());
        nestRepo.save(nest);
        channel.sendMessage("added nest " + nest.getName()).submit();
    }

    private void deleteNest(PrivateChannel channel, String[] options) {
        String nestName = options[0].trim();
        if (nestRepo.existsById(nestName)) {
            nestRepo.deleteById(options[0]);
            channel.sendMessage("deleted nest " + nestName).submit();

            pinNestList(channel);
        } else {
            channel.sendMessage("unknown nest " + nestName).submit();
        }
    }

    private void pinNestList(PrivateChannel channel) {
        Message allNestsMessage = buildAllNestMessage();
        TextChannel sightings = channel.getJDA().getTextChannelsByName("sightings", true).get(0);
        List<Message> pinnedMessages = sightings.getPinnedMessages().complete();
        pinnedMessages.stream().
                filter(message -> message.getMember().getUser().isBot()).
                forEach(message -> message.delete().submit());
        String id = sightings.sendMessage(allNestsMessage).complete().getId();
        sightings.pinMessageById(id ).submit();
    }

    private void updateNest(PrivateChannel channel, String memberName, String[] options) {
        String nestName = options[0].trim();
        Optional<Nest> maybeNest = nestRepo.findById(nestName);
        if (!maybeNest.isPresent()) {
            // HACK for lazy people that don't type Capital Letters
            maybeNest = StreamSupport.stream(nestRepo.findAll().spliterator(), false).
                    filter(nest -> nest.getName().equalsIgnoreCase(nestName)).
                    findAny();
            if (!maybeNest.isPresent()) {
                channel.sendMessage("unknown nest " + nestName).submit();
                return;
            }
        }

        Nest nest = maybeNest.get();
        String pokemonName = options[1].trim();
        nest.setPokemon(pokemonName);
        nest.setReportedBy(memberName);
        nestRepo.save(nest);
        channel.sendMessage("updated nest " + nest.getName() + " to " + pokemonName).submit();

        pinNestList(channel);
    }

    private void clearNests(PrivateChannel channel, String memberName) {
        LOG.info("{} cleared nests", memberName);
        Iterable<Nest> nests = nestRepo.findAll();
        ZonedDateTime now = ZonedDateTime.now();
        StreamSupport.stream(nests.spliterator(), false).forEach(nest -> {
            nest.setPokemon(null);
            nest.setReportedAt(now);
            nest.setReportedBy(memberName);
        });
        nestRepo.saveAll(nests);
        channel.sendMessage("nests cleared").submit();
    }
}
