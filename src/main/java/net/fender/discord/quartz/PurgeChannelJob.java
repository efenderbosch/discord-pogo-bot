package net.fender.discord.quartz;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.mutable.MutableInt;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurgeChannelJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeChannelJob.class);

    @Autowired
    private JDA jda;

    @Override
    public void execute(JobExecutionContext context) {
        String channelName = (String) context.getMergedJobDataMap().get("channel");
        List<TextChannel> channels = jda.getTextChannelsByName(channelName, true);
        for (TextChannel channel : channels) {
            LOG.info("purging {}", channel.getName());
            channel.sendTyping().submit();
            MutableInt count = new MutableInt();
            channel.getIterableHistory().stream().
                    filter(message -> !message.isPinned()).
                    peek(message -> count.increment()).
                    forEach(message -> message.delete().queue());
            LOG.info("deleted {} messages from {}", count.getValue(), channel.getName());
        }
    }
}
