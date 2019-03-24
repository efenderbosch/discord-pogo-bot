package net.fender.pogo;

import net.fender.spatial.GeometryUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringJUnitConfig
@EnableAutoConfiguration
@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.h2geodb.GeoDBDialect")
public class ReportRepositoryTest {

    @Autowired
    ReportRepository reportRepo;

    @Test
    public void test_deleteByReportedAtBeforeNow() {
        Report one = new Report();
        one.setReportedAt(LocalDate.now());
        one.setPokestop("pokestop");
        one.setReward("reward");
        one.setTask("task");
        one.setLocation(GeometryUtil.createPointFrom(41.132876, -81.548696));
        reportRepo.save(one);

        Report two = new Report();
        two.setReportedAt(LocalDate.now().minusDays(1));
        two.setPokestop("pokestop");
        two.setReward("reward");
        two.setTask("task");
        two.setLocation(GeometryUtil.createPointFrom(41.132876, -81.548696));
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
