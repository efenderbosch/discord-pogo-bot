package net.fender.discord.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QuartzProperties.class)
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
                withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 1)).
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
                withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 1)).
                build();
    }

}
