package by.it.group410972.margo.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
Даны события events
реализуйте метод calcStartTimes, так, чтобы число включений регистратора на
заданный период времени (1) было минимальным, а все события events
были зарегистрированы.
Алгоритм жадный. Для реализации обдумайте надежный шаг.
*/

public class A_VideoRegistrator {

    public static void main(String[] args) {
        A_VideoRegistrator instance = new A_VideoRegistrator();
        double[] events = new double[]{1, 1.1, 1.6, 2.2, 2.4, 2.7, 3.9, 8.1, 9.1, 5.5, 3.7};
        List<Double> starts = instance.calcStartTimes(events, 1); //рассчитаем моменты старта, с длиной сеанса 1
        System.out.println(starts); //покажем моменты старта
    }

    // Модификаторы доступа опущены для возможности тестирования
    List<Double> calcStartTimes(double[] events, double workDuration) {
        List<Double> result = new ArrayList<>();
        if (events == null || events.length == 0) {
            return result;
        }

        // Создаем копию массива для сортировки, чтобы не менять исходный
        double[] sortedEvents = events.clone();
        Arrays.sort(sortedEvents);

        int i = 0;
        int n = sortedEvents.length;

        while (i < n) {
            // Берем самое раннее не покрытое событие
            double startTime = sortedEvents[i];
            result.add(startTime);
            double endTime = startTime + workDuration;

            // Продвигаемся, пока события попадают в интервал [startTime, endTime]
            while (i < n && sortedEvents[i] <= endTime) {
                i++;
            }
        }

        return result;
    }
}