package net.fender.discord.quartz;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PurgeChannelProperties {

    private String channel;
    private ChronoUnit truncateTo;
    private Duration window;
    private String cron;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public ChronoUnit getTruncateTo() {
        return truncateTo;
    }

    public void setTruncateTo(ChronoUnit truncateTo) {
        this.truncateTo = truncateTo;
    }

    public Duration getWindow() {
        return window;
    }

    public void setWindow(Duration window) {
        this.window = window;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
