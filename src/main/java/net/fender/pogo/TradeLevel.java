package net.fender.pogo;

import java.util.Arrays;
import java.util.function.Predicate;

public enum TradeLevel implements Predicate<IndividualValues> {

    LUCKY_TRADE("lucky trade", 12),
    RAID_HATCH_RESEARCH("raid/hatch/research", 10),
    BEST_FRIEND("best friend", 5),
    WEATHER_BOOSTED("weather boosted", 4),
    ULTRA_FRIEND("ultra friend", 3),
    GREAT_FRIEND("great friend/purified", 2),
    GOOD_FRIEND("good friend", 1),
    WILD("wild catch", 0);

    public final String description;
    private final int floor;

    TradeLevel(String description, int floor) {
        this.description = description;
        this.floor = floor;
    }

    public static TradeLevel getTradeLevel(IndividualValues ivs) {
        return Arrays.stream(values()).filter(tradeLevel -> tradeLevel.test(ivs)).findFirst().orElseGet(null);
    }

    private static boolean testIVs(IndividualValues ivs, int floor) {
        return ivs.getAttack() >= floor &&
                ivs.getDefense() >= floor &&
                ivs.getStamina() >= floor;
    }

    @Override
    public boolean test(IndividualValues ivs) {
        return testIVs(ivs, floor);
    }
}
