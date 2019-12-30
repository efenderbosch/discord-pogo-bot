package net.fender.discord.quartz;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("quartz")
public class QuartzConfigurationProperties {

    private List<PurgeChannelProperties> purgeChannels = new ArrayList<>();

    public List<PurgeChannelProperties> getPurgeChannels() {
        return purgeChannels;
    }

    public void setPurgeChannels(List<PurgeChannelProperties> purgeChannels) {
        this.purgeChannels = purgeChannels;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
