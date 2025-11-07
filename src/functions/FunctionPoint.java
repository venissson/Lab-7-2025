package functions;
import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x;
    private double y;
    private static final long serialVersionUID = 1L;
    public FunctionPoint(double x, double y) { //Конструктор с заданными координатами
        this.x = x;
        this.y = y;
    }
    public FunctionPoint(FunctionPoint point) { //Конструктор копирования
        this.x = point.x;
        this.y = point.y;
    }
    public FunctionPoint() { //Конструктор по умолчанию
        this(0.0, 0.0);
    }
    public double getX() {  //Геттеры и сеттеры
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    @Override
    public String toString() { // Возвращаем точку в виде (x; y)
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;

        return Double.compare(that.x, x) == 0 && // Используем сравнение с учетом погрешности для double
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        // Преобразуем double в long битовое представление
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        // Разбиваем каждый long на два int (старшие и младшие 4 байта)
        int x1 = (int)(xBits & 0xFFFFFFFF);      // Младшие 4 байта x
        int x2 = (int)(xBits >>> 32);           // Старшие 4 байта x
        int y1 = (int)(yBits & 0xFFFFFFFF);      // Младшие 4 байта y
        int y2 = (int)(yBits >>> 32);           // Старшие 4 байта y

        // Комбинируем с помощью XOR
        return x1 ^ x2 ^ y1 ^ y2;
    }

    @Override
    public Object clone() {
        return new FunctionPoint(this); // Возвращает объект-копию для объекта точки
    }
}
