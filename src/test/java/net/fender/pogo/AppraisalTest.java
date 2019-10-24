package net.fender.pogo;

import org.junit.jupiter.api.Test;

import static net.fender.pogo.Appraisal.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppraisalTest {

    @Test
    public void test() {
        IndividualValues ivs = IndividualValues.ZERO;
        Appraisal appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(ZERO_STAR));

        ivs = new IndividualValues(7, 7, 8);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(ZERO_STAR));

        ivs = new IndividualValues(7, 8, 8);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(ONE_STAR));

        ivs = new IndividualValues(9, 10, 10);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(ONE_STAR));

        ivs = new IndividualValues(10, 10, 10);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(TWO_STAR));

        ivs = new IndividualValues(11, 12, 12);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(TWO_STAR));

        ivs = new IndividualValues(12, 12, 13);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(THREE_STAR));

        ivs = new IndividualValues(14, 15, 15);
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(THREE_STAR));

        ivs = IndividualValues.PERFECT;
        appraisal = Appraisal.appraise(ivs);
        assertThat(appraisal, is(FOUR_STAR));
    }
}
