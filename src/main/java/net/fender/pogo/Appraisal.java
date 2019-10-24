package net.fender.pogo;

import java.util.function.Predicate;

import static java.util.Arrays.stream;

public enum Appraisal implements Predicate<IndividualValues> {

    ZERO_STAR("0*", 0, 22),
    ONE_STAR("1*", 23, 29),
    TWO_STAR("2*", 30, 36),
    THREE_STAR("3*", 37, 44),
    FOUR_STAR("4*", 45, 45);

    private final String searchString;
    private final int min;
    private final int max;

    Appraisal(String searchString, int min, int max) {
        this.searchString = searchString;
        this.min = min;
        this.max = max;
    }

    public String getSearchString() {
        return searchString;
    }

    public static Appraisal appraise(IndividualValues ivs) {
        return stream(values()).filter(appraisal -> appraisal.test(ivs)).findAny().orElse(null);
    }

    @Override
    public boolean test(IndividualValues ivs) {
        int sum = ivs.getAttack() + ivs.getDefense() + ivs.getStamina();
        return min <= sum && sum <= max;
    }
}
