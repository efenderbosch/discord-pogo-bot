package net.fender.discord.quartz;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurgeChannelJob implements Job {

    private final JDA jda;

    @Autowired
    public PurgeChannelJob(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void execute(JobExecutionContext context) {
        String channelName = (String) context.getMergedJobDataMap().get("channel");
        List<TextChannel> channels = jda.getTextChannelsByName(channelName, true);
        for (TextChannel channel : channels) {
            channel.getIterableHistory().stream().
                    filter(message -> !message.isPinned()).
                    forEach(message -> message.delete().queue());
        }
    }
}
