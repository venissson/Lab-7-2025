package functions.basic;
import functions.Function;

public class Log implements Function {
    private double base;

    public Log(double base) {
        if (base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание логарифма должно быть положительным и не равным 1");
        }
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() { // Левая граница
        return 0.0; // Логарифм определен для x > 0
    }

    @Override
    public double getRightDomainBorder() { // Правая граница
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN; // Логарифм не определен для неположительных x
        }
        return Math.log(x) / Math.log(base); // Формула смены основания (то есть ln(x)/ln(base) = log base (x)
    }

    public double getBase() {
        return base;
    }
}