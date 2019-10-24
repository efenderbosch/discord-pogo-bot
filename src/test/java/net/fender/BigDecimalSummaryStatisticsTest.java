package net.fender;

import org.junit.jupiter.api.Test;

import java.util.List;

public class BigDecimalSummaryStatisticsTest {

    @Test
    public void test() {
        List<Long> test = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        BigDecimalSummaryStatistics stats =
                test.stream().collect(BigDecimalSummaryStatistics.DECIMAL64,
                BigDecimalSummaryStatistics::accept,
                BigDecimalSummaryStatistics::combine);
        System.out.println(stats);
    }
}
