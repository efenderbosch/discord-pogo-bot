package net.fender.pogo;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReportRepository extends CrudRepository<Report, Long> {

    long countByReportedAtBefore(LocalDate reportedAt);

    default long countByReportedAtBeforeNow() {
        return countByReportedAtBefore(LocalDate.now());
    }

    @Modifying
    @Query("delete from Report r where r.reported_at < :reportedAt")
    long deleteByReportedAtBefore(@Param("reportedAt") LocalDate reportedAt);

    default long deleteByReportedAtBeforeNow() {
        return deleteByReportedAtBefore(LocalDate.now());
    }
}
