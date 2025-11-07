package threads;

import functions.*;
import functions.basic.*;
import java.util.Random;

public class SimpleGenerator implements Runnable {
    private Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    public void run() {
        Random random = new Random();

        for (int i = 0; i < task.getTasksCount(); ++i) {
            synchronized (task) {
                // Логарифмическая функция со случайным основанием от 1 до 10
                double base = 1 + random.nextDouble() * 9;
                Function function = new Log(base);

                // Левая граница: [0, 100]
                double leftBorder = random.nextDouble() * 100;

                // Правая граница: [100, 200]
                double rightBorder = 100 + random.nextDouble() * 100;

                // Шаг дискретизации: [0.01, 1]
                double step = 0.01 + random.nextDouble() * 0.99;

                // Установка данных в задание
                task.setFunction(function);
                task.setLeftBorder(leftBorder);
                task.setRightBorder(rightBorder);
                task.setStep(step);

                // Вывод сообщения
                System.out.println("Source " + leftBorder + " " + rightBorder + " " + step);
            }
        }
    }
}
