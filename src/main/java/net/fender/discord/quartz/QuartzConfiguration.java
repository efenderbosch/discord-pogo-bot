package net.fender.discord.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
        JobDataMap jobDataMap = new JobDataMap();
        // TODO parameterize
        jobDataMap.put("channel", "sightings");
        jobDataMap.put("truncateTo", ChronoUnit.DAYS);
        jobDataMap.put("window", Duration.ofHours(0L));
        return JobBuilder.newJob().
                usingJobData(jobDataMap).
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
    public JobDetail purgeTeamRocketEncountersChannelJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        // TODO parameterize
        jobDataMap.put("channel", "team-rocket-encounters");
        jobDataMap.put("truncateTo", ChronoUnit.MINUTES);
        jobDataMap.put("window", Duration.ofMinutes(30L));
        return JobBuilder.newJob().
                usingJobData(jobDataMap).
                ofType(PurgeChannelJob.class).
                storeDurably().
                build();
    }

    @Bean
    public Trigger purgeTeamRocketEncountersChannelJobTrigger(@Qualifier("purgeTeamRocketEncountersChannelJobDetail") JobDetail
                                                                          purgeTeamRocketEncountersChannelJob) {
        return TriggerBuilder.newTrigger().forJob(purgeTeamRocketEncountersChannelJob).
                withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?")).
                build();
    }

}
