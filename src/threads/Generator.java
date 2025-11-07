package threads;

import functions.*;
import functions.basic.*;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private Task task;
    private Semaphore writeSemaphore;
    private Semaphore readSemaphore;

    public Generator(Task task, Semaphore writeSemaphore, Semaphore readSemaphore) {
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
                    System.out.println("Generator был прерван");
                    return;
                }

                // Генерация данных
                double base = 1 + Math.random() * 9;
                Function function = new Log(base);
                double leftBorder = Math.random() * 100;
                double rightBorder = 100 + Math.random() * 100;
                double step = 0.01 + Math.random() * 0.99;

                // Захват семафора записи
                writeSemaphore.acquire();
                try {
                    // Запись данных в задание
                    task.setFunction(function);
                    task.setLeftBorder(leftBorder);
                    task.setRightBorder(rightBorder);
                    task.setStep(step);

                    System.out.println("Source " + leftBorder + " " + rightBorder + " " + step);
                } finally {
                    // Разрешение чтения
                    readSemaphore.release();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Generator был прерван при ожидании семафора");
        }
    }
}
