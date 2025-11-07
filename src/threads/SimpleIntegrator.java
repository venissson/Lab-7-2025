package threads;

import functions.*;
import functions.basic.*;
import java.util.Random;

public class SimpleIntegrator implements Runnable {
    private Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    public void run() {
        for (int i = 0; i < task.getTasksCount(); ++i) {
            synchronized (task) {
                // Данные из задания
                double leftBorder = task.getLeftBorder();
                double rightBorder = task.getRightBorder();
                double step = task.getStep();
                Function function = task.getFunction();

                if (step <= 0) {
                    System.out.println("Пропущено задание с step=" + step);
                    continue;
                }
                if (function == null) {
                    System.out.println("Пропущено задание - функция не готова");
                    continue;
                }

                // Вычисление интеграла
                double result = Functions.integrate(function, leftBorder, rightBorder, step);

                // Вывод результата
                System.out.println("Result " + leftBorder + " " + rightBorder + " " + step + " " + result);
            }
        }
    }
}
