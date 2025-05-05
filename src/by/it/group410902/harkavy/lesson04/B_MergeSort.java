package by.it.group410902.harkavy.lesson04;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Реализуйте сортировку слиянием для одномерного массива.
Сложность алгоритма должна быть не хуже, чем O(n log n)

Первая строка содержит число 1<=n<=10000,
вторая - массив A[1…n], содержащий натуральные числа, не превосходящие 10E9.
Необходимо отсортировать полученный массив.

Sample Input:
5
2 3 9 2 9
Sample Output:
2 2 3 9 9
*/

public class B_MergeSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_MergeSort.class.getResourceAsStream("dataB.txt");
        B_MergeSort instance = new B_MergeSort();
        int[] result = instance.getMergeSort(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getMergeSort(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        // 1) читаем размер
        int n = scanner.nextInt();
        // 2) читаем сам массив
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        // 3) выполняем сортировку слиянием
        int[] tmp = new int[n];
        mergeSort(a, 0, n - 1, tmp);

        return a;
    }

    /**
     * Рекурсивный метод сортировки слиянием участка a[l..r].
     */
    private void mergeSort(int[] a, int l, int r, int[] tmp) {
        if (l >= r) {
            return;
        }
        int m = l + (r - l) / 2;
        // сортируем левую половину
        mergeSort(a, l, m, tmp);
        // сортируем правую половину
        mergeSort(a, m + 1, r, tmp);
        // объединяем две отсортированные части
        merge(a, l, m, r, tmp);
    }

    /**
     * Сливает два отсортированных подпоследовательности:
     * a[l..m] и a[m+1..r] в единый упорядоченный отрезок a[l..r].
     */
    private void merge(int[] a, int l, int m, int r, int[] tmp) {
        int i = l;        // указатель для левой части
        int j = m + 1;    // указатель для правой части
        int k = l;        // указатель для tmp

        // идём по обеим частям и берём меньший элемент
        while (i <= m && j <= r) {
            if (a[i] <= a[j]) {
                tmp[k++] = a[i++];
            } else {
                tmp[k++] = a[j++];
            }
        }
        // остатки из левой части
        while (i <= m) {
            tmp[k++] = a[i++];
        }
        // остатки из правой части
        while (j <= r) {
            tmp[k++] = a[j++];
        }
        // копируем обратно в a[l..r]
        for (k = l; k <= r; k++) {
            a[k] = tmp[k];
        }
    }
}
