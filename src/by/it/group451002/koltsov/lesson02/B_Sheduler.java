package by.it.group451002.koltsov.lesson02;


import java.util.ArrayList;
import java.util.List;
/*
Даны интервальные события events
реализуйте метод calcStartTimes, так, чтобы число принятых к выполнению
непересекающихся событий было максимально.
Алгоритм жадный. Для реализации обдумайте надежный шаг.
*/

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

    List<Event> calcStartTimes(Event[] events, int from, int to)
    {
        List<Event> result;
        result = new ArrayList<>();

        int i;
        int minInd;
        Event tempEvent;
        for (i = 0; i < events.length; i++)
        {
            minInd = i;

            for (int k = i + 1; k < events.length; k++)
                if (events[k].stop <= events[minInd].stop)
                    if (events[k].stop == events[minInd].stop && events[k].start < events[minInd].start)
                        minInd = k;
                    else if (events[k].stop < events[minInd].stop )
                        minInd = k;

            tempEvent = events[i];
            events[i] = events[minInd];
            events[minInd] = tempEvent;
        }

        int currTime = from;
        for (i = 0; i < events.length; i++)
            if (events[i].start >= currTime && events[i].stop <= to)
            {
                result.add(events[i]);
                currTime = events[i].stop;
            }
        return result;
    }

    //событие у аудитории(два поля: начало и конец)
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