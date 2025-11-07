package functions.basic;
import functions.Function;

public abstract class TrigonometricFunction implements Function{
    @Override
    public double getLeftDomainBorder() { // Левая граница
        return Double.NEGATIVE_INFINITY;
    }
    @Override
    public double getRightDomainBorder() { // Правая граница
        return Double.POSITIVE_INFINITY;
    }
    @Override
    public abstract double getFunctionValue(double x);
}
