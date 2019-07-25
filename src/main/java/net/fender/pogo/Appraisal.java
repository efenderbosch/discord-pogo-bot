package net.fender.pogo;

public enum Appraisal {

    ZERO_STAR("0*"),
    ONE_STAR("1*"),
    TWO_STAR("2*"),
    THREE_STAR("3*"),
    FOUR_STAR("4*");

    private final String searchString;

    Appraisal(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public static Appraisal appraise(IndividualValues ivs) {
        int sum = ivs.getAttack() + ivs.getDefense() + ivs.getStamina();
        if (sum == 45) return FOUR_STAR;
        if (sum >= 37) return THREE_STAR;
        if (sum >= 30) return TWO_STAR;
        if (sum >= 23) return ONE_STAR;
        return ZERO_STAR;
    }
}
