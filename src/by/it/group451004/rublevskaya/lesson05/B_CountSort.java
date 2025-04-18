package by.it.group451004.rublevskaya.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class B_CountSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_CountSort.class.getResourceAsStream("dataB.txt");
        B_CountSort instance = new B_CountSort();
        int[] result = instance.countSort(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] countSort(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        // Размер массива
        int n = scanner.nextInt();
        int[] points = new int[n];

        // Чтение точек
        for (int i = 0; i < n; i++) {
            points[i] = scanner.nextInt();
        }

        // Массив для подсчёта частот чисел (числа от 1 до 10)
        int[] count = new int[11]; // Индексы 0..10

        // Подсчёт частот
        for (int i = 0; i < n; i++) {
            count[points[i]]++;
        }

        // Восстановление отсортированного массива
        int index = 0;
        for (int value = 1; value <= 10; value++) {
            while (count[value] > 0) {
                points[index++] = value;
                count[value]--;
            }
        }

        return points;
    }
}