package functions;
import java.io.*;
import java.util.Iterator;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private FunctionPoint[] points; //Массив для хранения точек
    private int pointsCount; //Счетчик точек
    public ArrayTabulatedFunction() {} // Конструктор по умолчанию для Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) { //Конструктор по количеству точек
        if (leftX >= rightX) { //Проверка области определения
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10]; //Запас места
        double step = (rightX - leftX) / (pointsCount - 1); //Задаем шаг
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) { //Конструктор по значениям функции в виде массива
        if (leftX >= rightX) { //Проверка области определения
            throw new IllegalArgumentException("Левая граница области определения больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10]; //Запас места
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек меньше двух");
        }
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по значению абсциссы");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10]; // Запас места

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]); // Создаем копию точки
        }
    }
    public double getLeftDomainBorder() { //Метод для определения левой границы
        return points[0].getX();
    }

    public double getRightDomainBorder() { //Метод для определения правой границы
        return points[pointsCount - 1].getX();
    }
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder() || pointsCount == 0) {
            return Double.NaN;
        }
        for (int i = 0; i < pointsCount - 1; i++) { //Ищем интервал, в котором содержится х
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

            if (x >= x1 && x <= x2) { // Линейная интерполяция
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }
    public int getPointsCount() { //Получение количества точек
        return pointsCount;
    }
    public FunctionPoint getPoint(int index) { // Возврат копии точки
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return new FunctionPoint(points[index]);
    }
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException { //Замена значения абсциссы с указанным номером
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        double newX = point.getX();
        if (pointsCount == 1) { // Если всего 1 точка
            points[0] = new FunctionPoint(point);
            return;
        }
        if (index == 0) { // Проверка для первого элемента
            if (newX >= points[1].getX()) {
                throw new InappropriateFunctionPointException("Новая X для первой точки должна быть меньше следующей точки");
            }
        }
        else if (index == pointsCount - 1) { // Проверка для последнего элемента
            if (newX <= points[pointsCount - 2].getX()) { // строго больше предыдущего
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        else { // Проверка для средних элементов
            if (newX <= points[index - 1].getX() || newX >= points[index + 1].getX()) {
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        points[index] = new FunctionPoint(point);
    }
    public double getPointX(int index) { //Возврат значения абсциссы с указанным номером
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }

        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException { //Замена значения абсциссы с указанным номером
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        if (pointsCount == 1) { // Если всего 1 точка
            points[index].setX(x);
            return;
        }
        if (index == 0) { // Проверка для первого элемента
            if (x >= points[1].getX()) {
                throw new InappropriateFunctionPointException("Новая X для первой точки должна быть меньше следующей точки");
            }
        }
        else if (index == pointsCount - 1) { // Проверка для последнего элемента
            if (x <= points[pointsCount - 2].getX()) { // строго больше предыдущего
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        else { // Проверка для средних элементов
            if (x <= points[index - 1].getX() || x >= points[index + 1].getX()) {
                throw new InappropriateFunctionPointException("Новая X для последней точки должна быть больше предыдущей точки");
            }
        }
        points[index].setX(x);
    }

    public double getPointY(int index) { //Возврат значения ординаты с указанным номером
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) { //Замена значения ординаты с указанным номером
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        points[index].setY(y);
    }
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) { // Проверка на правильность индекса
            throw new FunctionPointIndexOutOfBoundsException("Индекс выходит за границы: " + index);
        }
        if (pointsCount < 3) { //Если количество точек меньше трех
            throw new IllegalStateException("Невозможно удалить точку: количество точек меньше трех");
        }
        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1); // Сдвиг точек влево, начиная с удаляемой
        pointsCount--;
    }
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++) { //Проверка на уникальность х
            if (points[i].getX() == point.getX()) {
                throw new InappropriateFunctionPointException("Точка с такой X уже существует");
            }
        }
        if (pointsCount >= points.length) { // Если массив заполнен, увеличиваю его
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }
        int insertIndex = 0; // Нахожу позицию для вставки
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }
        if (insertIndex < pointsCount && // Проверяю, не существует ли уже точка с таким X
                points[insertIndex].getX() == point.getX()) {
            return; // Точка с таким X уже существует
        }
        // Сдвиг точек вправо
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
    @Override
    public String toString() { // Вывод табулированной функции в сроку
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append("(").append(points[i].getX())
                    .append("; ").append(points[i].getY()).append(")");
            if (i < pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        // Если объект - ArrayTabulatedFunction, используется оптимизированное сравнение
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
            if (this.pointsCount != other.pointsCount) return false;

            // Прямое сравнение массивов точек
            for (int i = 0; i < pointsCount; i++) {
                if (!this.points[i].equals(other.points[i])) {
                    return false;
                }
            }
            return true;
        }

        // Если объект - другой реализации TabulatedFunction, используется общий подход
        if (o instanceof TabulatedFunction) {
            TabulatedFunction other = (TabulatedFunction) o;
            if (this.getPointsCount() != other.getPointsCount()) return false;

            // Сравниваем через getPoint()
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint thisPoint = this.getPoint(i);
                FunctionPoint otherPoint = other.getPoint(i);
                if (!thisPoint.equals(otherPoint)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
    @Override
    public int hashCode() {
        int hash = pointsCount; // Начало с количества точек
        // Комбинация хэш-кодов всех точек через XOR
        for (int i = 0; i < pointsCount; i++) {
            hash ^= points[i].hashCode();
        }
        return hash;
    }

    @Override
    public Object clone() { // Глубокое копирование
        // Создание массива
        FunctionPoint[] realPoints = new FunctionPoint[this.pointsCount];
        for (int i = 0; i < this.pointsCount; i++) {
            realPoints[i] = new FunctionPoint(this.points[i]);
        }

        // Временный объект через конструктор
        ArrayTabulatedFunction cloned = new ArrayTabulatedFunction(realPoints);

        return cloned;
    }
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                // Возвращение копии точки для сохранения инкапсуляции
                return new FunctionPoint(points[currentIndex++]);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }
    public void printTabulatedFunction() {

        int pointsCount = getPointsCount();
        for (int i = 0; i < pointsCount; i++) {
            double x = getPointX(i);
            double y = getPointY(i);
            System.out.println("x = " + x + ", y = " + y);
        }
    }
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }

}
