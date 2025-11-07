package threads;

import functions.Function;
import functions.Functions;
import functions.InappropriateFunctionPointException;
import java.util.concurrent.Semaphore;

public class Integrator extends Thread {
    private Task task;
    private Semaphore writeSemaphore;
    private Semaphore readSemaphore;

    public Integrator(Task task, Semaphore writeSemaphore, Semaphore readSemaphore) {
        this.task = task;
        this.writeSemaphore = writeSemaphore;
        this.readSemaphore = readSemaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); ++i) {
                // Проверка прерывания
                if (Thread.interrupted()) {
                    System.out.println("Integrator был прерван");
                    return;
                }

                // Ожидание данных для чтения
                readSemaphore.acquire();
                try {
                    // Чтение данных из задания
                    double leftBorder = task.getLeftBorder();
                    double rightBorder = task.getRightBorder();
                    double step = task.getStep();
                    Function function = task.getFunction();

                    // Проверка корректности данных
                    if (function == null || step <= 0) {
                        System.out.println("Пропущено задание - некорректные данные");
                        continue;
                    }

                    // Вычисление интеграла
                    double result = Functions.integrate(function, leftBorder, rightBorder, step);
                    System.out.println("Result " + leftBorder + " " + rightBorder + " " + step + " " + result);
                } finally {
                    // Разрешение записи
                    writeSemaphore.release();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator был прерван при ожидании семафора");
        }
    }
}
