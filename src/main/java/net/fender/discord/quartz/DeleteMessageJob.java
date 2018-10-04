package net.fender.discord.quartz;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.ErrorResponse;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteMessageJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteMessageJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        Message message = (Message) context.getMergedJobDataMap().get("message");
        if (message == null) return;
        try {
            message.delete().queue();
        } catch (ErrorResponseException e) {
            if (e.getErrorResponse() == ErrorResponse.UNKNOWN_CHANNEL) {
                LOG.info("error deleting message {}", message.getId());
            }
        }
    }
}
