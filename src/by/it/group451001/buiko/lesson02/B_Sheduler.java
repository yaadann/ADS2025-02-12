package by.it.group451001.buiko.lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class B_Sheduler {
    public static void main(String[] args) {
        B_Sheduler instance = new B_Sheduler();
        Event[] events = {new Event(0, 3), new Event(0, 1), new Event(1, 2), new Event(3, 5),
                new Event(1, 3), new Event(1, 3), new Event(1, 3), new Event(3, 6),
                new Event(2, 7), new Event(2, 3), new Event(2, 7), new Event(7, 9),
                new Event(3, 5), new Event(2, 4), new Event(2, 3), new Event(3, 7),
                new Event(4, 5), new Event(6, 7), new Event(6, 9), new Event(7, 9),
                new Event(8, 9), new Event(4, 6), new Event(8, 10), new Event(7, 10)
        };

        List<Event> starts = instance.calcStartTimes(events, 0, 10);  //рассчитаем оптимальное заполнение аудитории
        System.out.println(starts);                                 //покажем рассчитанный график занятий
    }

    List<Event> calcStartTimes(Event[] events, int from, int to) {
        // События, которые нужно распределить в аудитории в период [from, to] (включительно).
        // Оптимизация проводится по наибольшему числу непересекающихся событий.
        // Начало и конец событий могут совпадать.
        List<Event> result;
        result = new ArrayList<>();;


        Arrays.sort(events, (e1, e2) -> Integer.compare(e1.stop, e2.stop));

        int currentTime = from;

        for (Event event : events) {
            if (event.start >= currentTime && event.stop <= to) {
                result.add(event);
                currentTime = event.stop;
            }
        }

        return result; // Вернем итог
    }

    // Событие у аудитории (два поля: начало и конец)
    static class Event {
        int start;
        int stop;

        Event(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public String toString() {
            return "(" + start + ":" + stop + ")";
        }
    }
}
