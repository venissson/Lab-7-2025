package functions;

import java.io.*;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;

public class TabulatedFunctions {
    private TabulatedFunctions() {} // Приватный конструктор для предотвращения создания объекта

    // Фабрика по умолчанию
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    // Метод для установки фабрики
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    // Новые методы создания через фабрику
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    // Методы с рефлексией
    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass,
                                                            double leftX, double rightX, int pointsCount) {
        // Проверка, что класс реализует TabulatedFunction
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Class must implement TabulatedFunction interface");
        }

        try {
            // Поиск конструктора с параметрами (double, double, int)
            Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, int.class);
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating tabulated function", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass,
                                                            double leftX, double rightX, double[] values) {
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Class must implement TabulatedFunction interface");
        }

        try {
            // Поиск конструктора с параметрами (double, double, double[])
            Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, double[].class);
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating tabulated function", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, FunctionPoint[] points) {
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Class must implement TabulatedFunction interface");
        }

        try {
            // Поиск конструктора с параметрами (FunctionPoint[])
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating tabulated function", e);
        }
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Вызов фабрики вместо создания объекта
        TabulatedFunction tabulatedFunc = createTabulatedFunction(leftX, rightX, pointsCount);

        // Заполнение значениями функции
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }
    // Перегруженный метод tabulate с рефлексией
    public static TabulatedFunction tabulate(Class<?> functionClass,
                                             Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы выходят за область определения функции");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // Создание табулированной функции через рефлексию
        TabulatedFunction tabulatedFunc = createTabulatedFunction(functionClass, leftX, rightX, pointsCount);

        // Заполнение значениями функции
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }
    // Методы чтения с рефлексией
    public static TabulatedFunction inputTabulatedFunction(Class<?> functionClass, InputStream in) throws IOException {
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Class must implement TabulatedFunction interface");
        }

        DataInputStream dataIn = new DataInputStream(in);

        // Чтение количества точек
        int pointsCount = dataIn.readInt();

        // Чтение координат точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        // Создание объекта через рефлексию
        try {
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating tabulated function", e);
        }
    }

    public static TabulatedFunction readTabulatedFunction(Class<?> functionClass, Reader in) throws IOException {
        if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
            throw new IllegalArgumentException("Class must implement TabulatedFunction interface");
        }

        StreamTokenizer tokenizer = new StreamTokenizer(in);
        // Чтение количества точек
        tokenizer.nextToken();
        int pointsCount = (int) tokenizer.nval;
        // Чтение координат точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;

            tokenizer.nextToken();
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        // Создание объекта через рефлексию
        try {
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
            return (TabulatedFunction) constructor.newInstance((Object) points);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating tabulated function", e);
        }
    }
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException { //Метод вывода табулированной функции в байтовый поток
        DataOutputStream dataOut = new DataOutputStream(out);

        // Записываем количество точек
        dataOut.writeInt(function.getPointsCount());

        // Записываем координаты точек
        for (int i = 0; i < function.getPointsCount(); i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush(); // Не закрываем поток, чтобы пользователь мог продолжать с ним работать
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException { //Метод ввода табулированной функции из байтового потока
        DataInputStream dataIn = new DataInputStream(in);

        // Читаем количество точек
        int pointsCount = dataIn.readInt();

        // Читаем координаты точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
        return createTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException { //Метод записи табулированной функции в символьный поток
        PrintWriter writer = new PrintWriter(out);
        // Записываем количество точек
        writer.print(function.getPointsCount());
        writer.print(" ");
        // Записываем координаты точек через пробел
        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(function.getPointX(i));
            writer.print(" ");
            writer.print(function.getPointY(i));
            if (i < function.getPointsCount() - 1) {
                writer.print(" ");
            }
        }
        writer.flush(); // Не закрываем поток
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException { //Метод чтения табулированной функции из символьного потока
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        // Читаем количество точек
        tokenizer.nextToken();
        int pointsCount = (int) tokenizer.nval;
        // Читаем координаты точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;

            tokenizer.nextToken();
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        return createTabulatedFunction(points);
    }
}

