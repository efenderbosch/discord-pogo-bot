package net.fender.pogo;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

public enum TradeLevel implements Predicate<IndividualValues> {

    LUCKY_TRADE("lucky trade", 12),
    RAID_HATCH_RESEARCH("raid/hatch/research", 10),
    BEST_FRIEND("best friend", 5),
    WEATHER_BOOSTED("weather boosted", 4),
    ULTRA_FRIEND("ultra friend", 3),
    GREAT_FRIEND("great friend/purified", 2),
    GOOD_FRIEND("good friend", 1),
    WILD("wild catch", 0);

    public static final List<TradeLevel> REVERSED = (stream(values()).sorted(reverseOrder()).collect(toList()));

    public final String description;
    private final int floor;

    TradeLevel(String description, int floor) {
        this.description = description;
        this.floor = floor;
    }

    public static TradeLevel getTradeLevel(IndividualValues ivs) {
        return stream(values()).filter(tradeLevel -> tradeLevel.test(ivs)).findFirst().orElseGet(null);
    }

    private static boolean testIVs(IndividualValues ivs, int floor) {
        return ivs.getAttack() >= floor &&
                ivs.getDefense() >= floor &&
                ivs.getStamina() >= floor;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean test(IndividualValues ivs) {
        return testIVs(ivs, floor);
    }
}
