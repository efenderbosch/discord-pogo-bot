package net.fender.discord.quartz;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteChannelJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteChannelJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        Channel channel = (Channel) context.getMergedJobDataMap().get("channel");
        if (channel == null) return;
        try {
            channel.delete().queue();
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_CHANNEL) {
                LOG.info("error deleting channel {}", channel.getName());
            }
        }
    }
}
