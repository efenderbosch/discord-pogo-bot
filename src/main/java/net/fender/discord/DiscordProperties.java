package net.fender.discord;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

    //@NotNull
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        String redactedToken = StringUtils.abbreviate(token, "...", 9);
        return new ToStringBuilder(this).append("token", redactedToken).toString();
    }
}
