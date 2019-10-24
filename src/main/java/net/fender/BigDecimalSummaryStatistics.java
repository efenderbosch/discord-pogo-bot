package net.fender;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

public class BigDecimalSummaryStatistics implements LongConsumer, IntConsumer, Consumer<BigDecimal> {

    public static final Supplier<BigDecimalSummaryStatistics> DECIMAL32 =
            () -> new BigDecimalSummaryStatistics(MathContext.DECIMAL32);

    public static final Supplier<BigDecimalSummaryStatistics> DECIMAL64 =
            () -> new BigDecimalSummaryStatistics(MathContext.DECIMAL64);

    public static final Supplier<BigDecimalSummaryStatistics> DECIMAL128 =
            () -> new BigDecimalSummaryStatistics(MathContext.DECIMAL128);

    private final MathContext mathContext;
    private BigDecimal count = BigDecimal.ZERO;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = BigDecimal.valueOf(Long.MAX_VALUE);
    private BigDecimal max = BigDecimal.valueOf(Long.MIN_VALUE);
    private BigDecimal sumOfSquares = BigDecimal.ZERO;

    private BigDecimalSummaryStatistics(MathContext mathContext) {
        this.mathContext = mathContext;
    }

    @Override
    public void accept(int value) {
        accept(BigDecimal.valueOf(value));
    }

    @Override
    public void accept(long value) {
        accept(BigDecimal.valueOf(value));
    }

    @Override
    public void accept(BigDecimal value) {
        count = count.add(BigDecimal.ONE);
        sum = sum.add(value);
        min = min.min(value);
        max = max.max(value);

        BigDecimal valueSquared = value.pow(2);
        sumOfSquares = sumOfSquares.add(valueSquared);
    }

    public BigDecimal getCount() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public final BigDecimal getAverage() {
        return count.longValue() > 0 ? sum.divide(count, mathContext) : BigDecimal.ZERO;
    }

    public final BigDecimal getStandardDeviation() {
        if (count.longValue() == 0) return BigDecimal.ZERO;

        // Math.sqrt((getSumOfSquare() / getCount()) - Math.pow(getAverage(), 2))
        BigDecimal termOne = sumOfSquares.divide(count, mathContext);
        BigDecimal average = getAverage();
        BigDecimal termTwo = average.pow(2);
        return termOne.subtract(termTwo).sqrt(mathContext);
    }

    public void combine(BigDecimalSummaryStatistics other) {
        count.add(other.count);
        sum = sum.add(other.sum);
        sumOfSquares = sumOfSquares.add(other.sumOfSquares);
        min = min.min(other.min);
        max = max.max(other.max);
    }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%f, sum=%f, min=%f, average=%f, max=%f, std dev=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax(),
                getStandardDeviation());
    }
}
