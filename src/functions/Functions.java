package functions;
import functions.meta.*;

public class Functions {
    private Functions() {} // Приватный конструктор для предотвращения создания объекта
    public static Function shift(Function f, double shiftX, double shiftY){ //возвращает объект функции, полученной из исходной сдвигом вдоль осей
        return new Shift(f, shiftX, shiftY);
    };
    public static Function scale(Function f, double kX, double kY){ //возвращает объект функции, полученной из исходной масштабированием вдоль осей
        return new Scale(f, kX, kY);
    };
    public static Function power(Function f, double pow) { //возвращает объект функции, являющейся заданной степенью исходной
        return new Power(f, pow);
    };
    public static Function sum(Function f1, Function f2) { //возвращает объект функции, являющейся суммой двух исходных
        return new Sum(f1, f2);
    };
    public static Function mult(Function f1, Function f2) { //возвращает объект функции, являющейся произведением двух исходных
        return new Mult(f1, f2);
    };
    public static Function composition(Function f1, Function f2) { //возвращает объект функции, являющейся композицией двух исходных
        return new Composition(f1, f2);
    };
    public static double integrate(Function function, double leftBorder, double rightBorder, double step) {
        // Проверка корректности параметров
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг интегрирования должен быть положительным");
        }
        if (leftBorder >= rightBorder) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        // Проверка области определения
        if (leftBorder < function.getLeftDomainBorder() || rightBorder > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы области определения функции");
        }

        double integral = 0.0;
        double currentX = leftBorder;
        // Первая точка
        double prevY = function.getFunctionValue(currentX);
        // Проход по всем отрезкам
        while (currentX < rightBorder) {
            double nextX = Math.min(currentX + step, rightBorder);
            double nextY = function.getFunctionValue(nextX);

            // Площадь трапеции: (a + b) * h / 2
            integral += (prevY + nextY) * (nextX - currentX) / 2.0;

            currentX = nextX;
            prevY = nextY;
        }

        return integral;
    }
}
