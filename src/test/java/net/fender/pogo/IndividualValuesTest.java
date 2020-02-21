package net.fender.pogo;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class IndividualValuesTest {

    @Test
    public void test_space() {
        IndividualValues ivs = IndividualValues.parse("1  14  15");
        assertAll(
                () -> assertThat(ivs.getAttack(), is(1)),
                () -> assertThat(ivs.getDefense(), is(14)),
                () -> assertThat(ivs.getStamina(), is(15))
        );
    }

    @Test
    public void test_slash() {
        IndividualValues ivs = IndividualValues.parse("01/14/15");
        assertAll(
                () -> assertThat(ivs.getAttack(), is(1)),
                () -> assertThat(ivs.getDefense(), is(14)),
                () -> assertThat(ivs.getStamina(), is(15))
        );
    }

    @Test
    public void test_bad() {
        IndividualValues ivs = IndividualValues.parse("1 25 15");
        assertAll(
                () -> assertThat(ivs.getAttack(), is(0)),
                () -> assertThat(ivs.getDefense(), is(0)),
                () -> assertThat(ivs.getStamina(), is(0))
        );
    }

}
