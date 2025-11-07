package functions.meta;
import functions.Function;

public class Power implements Function {
    private Function f;
    private double pow;

    public Power(Function f, double pow) {
        this.f = f;
        this.pow = pow;
    }
    @Override
    public double getLeftDomainBorder() { // Левая граница
        return f.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() { // Правая граница
        return f.getRightDomainBorder();
    }
    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) { // Проверка на диапазон
            return Double.NaN;
        }
        return Math.pow(f.getFunctionValue(x), pow);
    }
}
