package net.fender.discord.listeners;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.fender.discord.filters.MemberHasRoleFilter;
import net.fender.pogo.Nest;
import net.fender.pogo.NestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

@Component
public class NestCommandListener extends CommandEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(NestCommandListener.class);

    private static final Pattern NEST = Pattern.compile("\\$nest\\s+(\\w+)\\s*(.*)");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
    private static final MemberHasRoleFilter ADMIN_USER = new MemberHasRoleFilter(".*-admin");

    private final NestRepository nestRepo;

    @Autowired
    public NestCommandListener(NestRepository nestRepo) {
        super(NEST);//, ADMIN_USER);
        this.nestRepo = nestRepo;
    }

    @Override
    protected void processCommand(MessageReceivedEvent event, List<String> parts) {
        TextChannel channel = event.getTextChannel();

        String memberName = event.getMember().getEffectiveName();
        RequestFuture<PrivateChannel> privateChannelFuture = event.getMember().getUser().openPrivateChannel().submit();
        PrivateChannel privateChannel;
        try {
            privateChannel = privateChannelFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            channel.sendMessage("could not open private channel with " + memberName);
            return;
        }

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
            updateNest(privateChannel, options);
        } else {
            channel.sendMessage("unknown $nest command").submit();
        }
    }

    private void sendHelp(PrivateChannel privateChannel) {
        privateChannel.sendMessage("nest commands:\n" +
                "'$nest add name, longitude, latitude' to add a new nest\n" +
                "'$nest update name, pokemon' to update a nest to a new pokemon").submit();
    }

    private void listAll(PrivateChannel channel) {
        LOG.info("listing nests");
        Iterable<Nest> nests = nestRepo.findAll();

        MessageBuilder builder = new MessageBuilder();
        for (Nest nest : nests) {
            builder.append(nest.getName()).append("\t");
            String pokemon = nest.getPokemon();
            if (pokemon == null) {
                builder.append("unknown\t");
            } else {
                builder.append(nest.getPokemon()).append("\t");
            }
            builder.append(nest.getReportedBy()).append("\t");
            builder.append(nest.getReportedAt().format(FORMATTER)).append("\n");
        }
        if (builder.isEmpty()) {
            LOG.info("no nests");
            channel.sendMessage("No nests! Use $nest add <name>, <longitude>, <latitude> to add a nest.").submit();
        } else {
            channel.sendMessage(builder.build()).submit();
        }
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
        } else {
            channel.sendMessage("unknown nest " + nestName).submit();
        }
    }

    private void updateNest(PrivateChannel channel, String[] options) {
        String nestName = options[0].trim();
        Optional<Nest> maybeNest = nestRepo.findById(nestName);
        if (!maybeNest.isPresent()) {
            channel.sendMessage("unknown nest " + nestName).submit();
            return;
        }

        Nest nest = maybeNest.get();
        String pokemonName = options[1].trim();
        nest.setPokemon(pokemonName);
        nestRepo.save(nest);
        channel.sendMessage("updated nest " + nestName + " to " + pokemonName).submit();
    }
}
