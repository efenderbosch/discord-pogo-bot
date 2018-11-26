package net.fender.pogo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestRepository extends CrudRepository<Quest, Long> {

    @Query("select * from Quest q where q.emote = :emote")
    List<Quest> findByEmote(@Param("emote") String emote);
}
