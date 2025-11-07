import functions.*;
import functions.basic.*;
import functions.meta.*;
import threads.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    public static void nonThread() {
        System.out.println("nonThread");
        // Объект задания
        Task task = new Task();
        task.setTasksCount(100); // Минимум 100 заданий

        Random random = new Random();

        for (int i = 0; i < task.getTasksCount(); i++) {
            try {
                // Логарифмическая функция со случайным основанием от 1 до 10
                double base = 1 + random.nextDouble() * 9;
                Function logFunction = new Log(base);
                task.setFunction(logFunction);

                // Левая граница: [0, 100]
                double leftBorder = random.nextDouble() * 100;
                task.setLeftBorder(leftBorder);

                // Правая граница: [100, 200]
                double rightBorder = 100 + random.nextDouble() * 100;
                task.setRightBorder(rightBorder);

                // Шаг дискретизации: [0, 1]
                double step = 0.01 + random.nextDouble() * 0.99;
                task.setStep(step);

                // Вывод информации о задании
                System.out.println("Source LeftBorder: " + task.getLeftBorder()
                        + " RightBorder: " + task.getRightBorder() + " Step: " + task.getStep() + "\n");

                // Вычисление интеграла
                double result = Functions.integrate(task.getFunction(),
                        task.getLeftBorder(), task.getRightBorder(), task.getStep());

                // Вывод результата
                System.out.println("Result LeftBorder: " + task.getLeftBorder()
                        + " RightBorder: " + task.getRightBorder() + " Step: " + task.getStep() + " Result: " + result + "\n");

            } catch (Exception e) {
                System.out.println("Ошибка в задании " + i + ": " + e.getMessage());
            }
        }
    }
    public static void simpleThreads() {
        System.out.println("simpleThreads");
        // Создание объекта задания
        Task task = new Task();
        // Количество выполняемых заданий (минимум 100)
        task.setTasksCount(100);
        // Создание потоков вычислений
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));
        // Запуск потоков
        generatorThread.start();
        integratorThread.start();


    }
    public static void complicatedThreads() {
        System.out.println("complicatedThreads");
        // Объект задания
        Task task = new Task();
        task.setTasksCount(100);

        // Два семафора:
        // writeSemaphore начинается с 1 разрешения (можно писать)
        // readSemaphore начинается с 0 разрешений (нельзя читать)
        Semaphore writeSemaphore = new Semaphore(1);
        Semaphore readSemaphore = new Semaphore(0);

        // Создание потоков
        Generator generator = new Generator(task, writeSemaphore, readSemaphore);
        Integrator integrator = new Integrator(task, writeSemaphore, readSemaphore);

        // Запуск потоков
        generator.start();
        integrator.start();

        try {
            // 50 миллисекунд
            Thread.sleep(50);

            // Прерывание потоки
            generator.interrupt();
            integrator.interrupt();

            // Ожидание завершения потоков
            generator.join();
            integrator.join();

        } catch (InterruptedException e) {
            System.out.println("Основной поток был прерван");
        }
    }
    public static void main(String[] args) {
        // Тестирование интегрирования экспоненты на отрезке [0, 1]
        Function expFunction = new Exp();
        double theoreticalValue = Math.E - 1; // интеграл (e^x) dx от 0 до 1 = e - 1
        double integralValue = Functions.integrate(expFunction, 0, 1, 0.0001);

        System.out.println("Теоретическое значение: " + theoreticalValue);
        System.out.println("Значение, полученное при помощи функции: " + integralValue);
        System.out.println("Шаг = " + 0.0001 + "\n");

        //nonThread();
        simpleThreads();
        //complicatedThreads();
    }
}