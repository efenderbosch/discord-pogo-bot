package net.fender.pogo;

import org.junit.jupiter.api.Test;

import static net.fender.pogo.TradeLevel.GOOD_FRIEND;
import static net.fender.pogo.TradeLevel.WILD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TradeLevelTest {

    @Test
    public void test_wild() {
        IndividualValues ivs = new IndividualValues(0, 0, 0);
        assertThat(TradeLevel.getTradeLevel(ivs), is(WILD));
    }

    @Test
    public void test_good_friend() {
        IndividualValues ivs = new IndividualValues(1, 1, 1);
        assertThat(TradeLevel.getTradeLevel(ivs), is(GOOD_FRIEND));
    }
}
