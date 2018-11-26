package net.fender.discord.quartz;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class PurgeChannelJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeChannelJob.class);

    @Autowired
    private JDA jda;

    public void setJda(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void execute(JobExecutionContext context) {
        String channelName = (String) context.getMergedJobDataMap().get("channel");
        List<TextChannel> channels = jda.getTextChannelsByName(channelName, true);
        OffsetDateTime midnight = OffsetDateTime.now().truncatedTo(DAYS);
        for (TextChannel channel : channels) {
            LOG.info("purging messages before {} in {}", midnight, channelName);
            channel.sendTyping().submit();
            int count = 0;
            for (Message message : channel.getIterableHistory()) {
                if (message.isPinned()) continue;

                if (message.getCreationTime().isAfter(midnight)) continue;

                message.delete().submit();
                count++;
            }
            LOG.info("deleted {} messages from {}", count, channel.getName());
        }
    }
}
