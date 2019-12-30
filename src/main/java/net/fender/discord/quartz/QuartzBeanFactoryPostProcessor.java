package net.fender.discord.quartz;

import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.List;

public class QuartzBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

//    private final QuartzConfigurationProperties quartzConfigurationProperties;

    public QuartzBeanFactoryPostProcessor(QuartzConfigurationProperties quartzConfigurationProperties) {
//        this.quartzConfigurationProperties = quartzConfigurationProperties;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        QuartzConfigurationProperties quartzConfigurationProperties =
                beanFactory.getBean(QuartzConfigurationProperties.class);
        List<PurgeChannelProperties> purgeChannelProperties = quartzConfigurationProperties.getPurgeChannels();
        for (PurgeChannelProperties props : purgeChannelProperties) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("channel", props.getChannel());
            jobDataMap.put("truncateTo", props.getTruncateTo());
            jobDataMap.put("window", props.getWindow());
            JobDetail jobDetail = JobBuilder.newJob().
                    usingJobData(jobDataMap).
                    ofType(PurgeChannelJob.class).
                    storeDurably().
                    build();
            Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail).
                    withSchedule(CronScheduleBuilder.cronSchedule(props.getCron())).
                    build();
            String beanName = props.getChannel() + props.getTruncateTo() + props.getWindow() + "QuartzTrigger";
            beanFactory.registerSingleton(beanName, trigger);
        }
    }
}
