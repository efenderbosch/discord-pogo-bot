package net.fender.discord.pogo;

import net.fender.pogo.Report;
import net.fender.pogo.ReportRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled
//@SpringJUnitConfig
//@DataJdbcTest
public class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepo;

    @Test
    public void test_deleteByReportedAtBeforeNow() {
        Report one = new Report();
        one.setReportedAt(LocalDate.now());
        one.setPokestop("pokestop");
        one.setReward("reward");
        one.setTask("task");
        one.setLatitude(1.1);
        one.setLongitude(2.2);
        reportRepo.save(one);

        Report two = new Report();
        two.setReportedAt(LocalDate.now().minusDays(1));
        two.setPokestop("pokestop");
        two.setReward("reward");
        two.setTask("task");
        two.setLatitude(1.1);
        two.setLongitude(2.2);
        reportRepo.save(two);

        long count = reportRepo.count();
        assertThat(count, is(2L));

        long deleted = reportRepo.deleteByReportedAtBeforeNow();
        assertThat(deleted, is(1L));

        count = reportRepo.count();
        assertThat(count, is(1L));

        Optional<Report> maybeOne = reportRepo.findById(one.getId());
        Optional<Report> maybeTwo = reportRepo.findById(two.getId());
        assertThat(maybeOne.isPresent(), is(true));
        assertThat(maybeTwo.isPresent(), is(false));
    }
}
