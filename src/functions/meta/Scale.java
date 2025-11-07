package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function f;
    private double kX;
    private double kY;

    public Scale(Function f, double kX, double kY) {
        this.f = f;
        this.kX = kX;
        this.kY = kY;
    }

    @Override
    public double getLeftDomainBorder() { // Левая граница
        if (kX > 0) {
            return f.getLeftDomainBorder() / kX;
        } else if (kX < 0) {
            return f.getRightDomainBorder() / kX;
        } else {
            return Double.NaN; // Масштаб 0 недопустим
        }
    }

    @Override
    public double getRightDomainBorder() { // Правая граница
        if (kX > 0) {
            return f.getRightDomainBorder() / kX;
        } else if (kX < 0) {
            return f.getLeftDomainBorder() / kX;
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        return kY * f.getFunctionValue(x * kX);
    }
}
