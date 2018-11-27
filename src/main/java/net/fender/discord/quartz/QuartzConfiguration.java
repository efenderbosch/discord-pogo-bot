package net.fender.discord.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;

@Configuration
public class QuartzConfiguration {

    @Bean
    public JobDetail purgeQuestChannelJobDetail() {
        return JobBuilder.newJob().
                usingJobData("channel", "quests").
                ofType(PurgeChannelJob.class).
                storeDurably().
                build();
    }

    @Bean
    public Trigger purgeQuestChannelJobTrigger(@Qualifier("purgeQuestChannelJobDetail") JobDetail
                                                       purgeQuestChannelJob) {
        return TriggerBuilder.newTrigger().forJob(purgeQuestChannelJob).
                withSchedule(dailyAtHourAndMinute(0, 1)).
                build();
    }

    @Bean
    public JobDetail purgeSightingsChannelJobDetail() {
        return JobBuilder.newJob().
                usingJobData("channel", "sightings").
                ofType(PurgeChannelJob.class).
                storeDurably().
                build();
    }

    @Bean
    public Trigger purgeSightingsChannelJobTrigger(@Qualifier("purgeSightingsChannelJobDetail") JobDetail
                                                           purgeSightingsChannelJob) {
        return TriggerBuilder.newTrigger().forJob(purgeSightingsChannelJob).
                withSchedule(dailyAtHourAndMinute(0, 1)).
                build();
    }

    @Bean
    public JobDetail purgeReportsJobDetail() {
        return JobBuilder.newJob().
                ofType(PurgeReportsJob.class).
                storeDurably().
                build();
    }

    @Bean
    public Trigger purgeReportsJobTrigger(@Qualifier("purgeReportsJobDetail") JobDetail purgeReportsJob) {
        return TriggerBuilder.newTrigger().forJob(purgeReportsJob).
                withSchedule(dailyAtHourAndMinute(0, 1)).
                build();
    }

}
