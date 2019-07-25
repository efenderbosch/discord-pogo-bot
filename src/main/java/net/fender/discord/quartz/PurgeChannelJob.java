package net.fender.discord.quartz;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String channelName = (String) jobDataMap.get("channel");
        List<TextChannel> channels = jda.getTextChannelsByName(channelName, true);
        ChronoUnit trunateTo = (ChronoUnit) jobDataMap.get("truncateTo");
        Duration window = (Duration) jobDataMap.get("window");
        OffsetDateTime purgeTime = OffsetDateTime.now().truncatedTo(trunateTo).minus(window);
        for (TextChannel channel : channels) {
            LOG.info("purging messages before {} in {}", purgeTime, channelName);
            int count = 0;
            for (Message message : channel.getIterableHistory()) {
                if (message.isPinned() || message.getCreationTime().isAfter(purgeTime)) continue;

                message.delete().submit();
                count++;
            }
            LOG.info("deleted {} messages from {}", count, channel.getName());
        }
    }
}
