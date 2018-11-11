package net.fender.pogo;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestRepository extends CrudRepository<Quest, Long> {
    List<Quest> findByEmote(String emote);
}
