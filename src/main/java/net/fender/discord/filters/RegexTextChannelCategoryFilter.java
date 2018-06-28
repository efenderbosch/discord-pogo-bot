package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

import java.util.regex.Pattern;

public class RegexTextChannelCategoryFilter implements TextChannelCategoryFilter {

    private final Pattern[] channelCategoryPatterns;

    public RegexTextChannelCategoryFilter(String... channelCategoryPatterns) {
    int length = channelCategoryPatterns.length;
        this.channelCategoryPatterns = new Pattern[length];
        for (int i = 0; i < length; i++) {
            this.channelCategoryPatterns[i] = Pattern.compile(channelCategoryPatterns[i]);
        }
    }

    @Override
    public boolean test(GenericMessageEvent event) {
        TextChannel textChannel = event.getTextChannel();
        if (textChannel == null) return false;

        Category category = textChannel.getParent();
        if (category == null) return false;

        String categoryName = category.getName();
        if (categoryName == null) return false;

        for (Pattern channelCategoryPatterns : channelCategoryPatterns) {
            if (channelCategoryPatterns.matcher(categoryName).matches()) {
                return true;
            }
        }

        return false;
    }
}
