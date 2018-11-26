package net.fender.discord.quartz;

import net.fender.pogo.ReportRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PurgeReportsJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeReportsJob.class);

    @Autowired
    private ReportRepository reportRepo;

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("purging reports");
        long count = reportRepo.deleteByReportedAtBeforeNow();
        LOG.info("purged {} reports", count);
        // TODO upload empty feature array to S3
    }
}
