package by.it.group410902.harkavy.lesson04;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Рассчитать число инверсий одномерного массива.
Сложность алгоритма должна быть не хуже, чем O(n log n)

Первая строка содержит число 1<=n<=10000,
вторая - массив A[1…n], содержащий натуральные числа, не превосходящие 10E9.
Необходимо посчитать число пар индексов 1<=i<j<n, для которых A[i]>A[j].

    (Такая пара элементов называется инверсией массива.
    Количество инверсий в массиве является в некотором смысле
    его мерой неупорядоченности: например, в упорядоченном по неубыванию
    массиве инверсий нет вообще, а в массиве, упорядоченном по убыванию,
    инверсию образуют каждые (т.е. любые) два элемента.
    )

Sample Input:
5
2 3 9 2 9
Sample Output:
2

Головоломка (т.е. не обязательно).
Попробуйте обеспечить скорость лучше, чем O(n log n) за счет многопоточности.
Докажите рост производительности замерами времени.
Большой тестовый массив можно прочитать свой или сгенерировать его программно.
*/


public class C_GetInversions {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_GetInversions.class.getResourceAsStream("dataC.txt");
        C_GetInversions instance = new C_GetInversions();
        int result = instance.calc(stream);
        System.out.print(result);
    }

    int calc(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);
        // читаем вход
        int n = scanner.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        // вспомогательный буфер
        int[] tmp = new int[n];
        // делаем подсчёт инверсий
        long invCount = countInversions(a, tmp, 0, n - 1);
        // гарантированно уложится в int для n<=10000
        return (int) invCount;
    }

    /**
     * Рекурсивно делит массив и считает инверсии.
     * @return число инверсий в a[l..r]
     */
    private long countInversions(int[] a, int[] tmp, int l, int r) {
        if (l >= r) {
            return 0;
        }
        int m = l + (r - l) / 2;
        long count = 0;
        // инверсии в левой половине
        count += countInversions(a, tmp, l, m);
        // инверсии в правой половине
        count += countInversions(a, tmp, m + 1, r);
        // кросс-инверсии при слиянии
        count += mergeAndCount(a, tmp, l, m, r);
        return count;
    }

    /**
     * Сливает отсортированные участки a[l..m] и a[m+1..r],
     * считая «кросс-инверсии» — пары (i,j), где i в левой, j в правой,
     * а a[i]>a[j].
     * @return число таких инверсий
     */
    private long mergeAndCount(int[] a, int[] tmp, int l, int m, int r) {
        int i = l;       // указатель по левой части
        int j = m + 1;   // указатель по правой части
        int k = l;       // указатель по tmp
        long inversions = 0;

        while (i <= m && j <= r) {
            if (a[i] <= a[j]) {
                tmp[k++] = a[i++];
            } else {
                // все оставшиеся a[i..m] формируют инверсии с a[j]
                inversions += (m - i + 1);
                tmp[k++] = a[j++];
            }
        }
        // докидываем остатки
        while (i <= m) {
            tmp[k++] = a[i++];
        }
        while (j <= r) {
            tmp[k++] = a[j++];
        }
        // копируем обратно в a[l..r]
        for (int p = l; p <= r; p++) {
            a[p] = tmp[p];
        }
        return inversions;
    }
}
