package net.fender.discord.filters;

import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.function.Predicate;

public enum HasImageAttachmentFilter implements Predicate<MessageReceivedEvent> {

    HAS_IMAGE_ATTACHMENT_FILTER;

    @Override
    public boolean test(MessageReceivedEvent event) {
        return event.getMessage().getAttachments().stream().filter(Attachment::isImage).findAny().isPresent();
    }
}
