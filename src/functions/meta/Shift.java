package functions.meta;

import functions.Function;

public class Shift implements Function {
    private Function f;
    private double shiftX;
    private double shiftY;

    public Shift(Function f, double shiftX, double shiftY) {
        this.f = f;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public double getLeftDomainBorder() { // Левая граница
        return f.getLeftDomainBorder() - shiftX;
    }

    @Override
    public double getRightDomainBorder() { // Правая граница
        return f.getRightDomainBorder() - shiftX;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x + shiftX < getLeftDomainBorder() || x + shiftX > getRightDomainBorder()) {
            return Double.NaN;
        }
        return f.getFunctionValue(x + shiftX) + x + shiftY;
    }
}
