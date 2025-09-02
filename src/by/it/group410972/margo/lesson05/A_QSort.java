package by.it.group410972.margo.lesson05;



import java.io.InputStream;
import java.util.Scanner;
import java.util.Arrays;

public class A_QSort {

    public static void main(String[] args) {
        InputStream stream = A_QSort.class.getResourceAsStream("dataA.txt");
        A_QSort instance = new A_QSort();
        int[] result = instance.getAccessory(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        Segment[] segments = new Segment[n];
        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        // читаем отрезки
        for (int i = 0; i < n; i++) {
            segments[i] = new Segment(scanner.nextInt(), scanner.nextInt());
        }
        // читаем точки
        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        // массивы начал и концов отрезков
        int[] starts = new int[n];
        int[] stops = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = segments[i].start;
            stops[i] = segments[i].stop;
        }
        Arrays.sort(starts);
        Arrays.sort(stops);

        // для каждой точки бинарный поиск: сколько начал <= point и сколько концов < point
        for (int i = 0; i < m; i++) {
            int left = lowerBound(starts, points[i]+1); // сколько отрезков с началом <= point
            int right = lowerBound(stops, points[i]);   // сколько отрезков с концом < point
            result[i] = left - right;
        }
        return result;
    }

    // возвращает количество элементов < value
    private int lowerBound(int[] arr, int value) {
        int left = 0, right = arr.length;
        while (left < right) {
            int mid = (left + right) / 2;
            if (arr[mid] < value)
                left = mid + 1;
            else
                right = mid;
        }
        return left;
    }

    // отрезок
    private static class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int a, int b) {
            // гарантирует start <= stop
            this.start = Math.min(a, b);
            this.stop = Math.max(a, b);
        }

        @Override
        public int compareTo(Segment o) {
            if (this.start != o.start) {
                return Integer.compare(this.start, o.start);
            }
            return Integer.compare(this.stop, o.stop);
        }
    }
}