package net.fender.pogo;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ReportRepository extends CrudRepository<Report, Long> {

    long countByReportedAtBefore(LocalDate reportedAt);

    default long countByReportedAtBeforeNow() {
        return countByReportedAtBefore(LocalDate.now());
    }

    long deleteByReportedAtBefore(LocalDate reportedAt);

    default long deleteByReportedAtBeforeNow() {
        return deleteByReportedAtBefore(LocalDate.now());
    }
}
