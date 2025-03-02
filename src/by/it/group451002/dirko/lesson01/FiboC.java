package by.it.group451002.dirko.lesson01;

/*
 * Даны целые числа 1<=n<=1E18 и 2<=m<=1E5,
 * необходимо найти остаток от деления n-го числа Фибоначчи на m
 * время расчета должно быть не более 2 секунд
 */

public class FiboC {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        FiboC fibo = new FiboC();
        int n = 55555;
        int m = 1000;
        System.out.printf("fasterC(%d)=%d \n\t time=%d \n\n", n, fibo.fasterC(n, m), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    long fasterC(long n, int m) {
        int First = 0;
        int Second = 1;
        int Third;
        int period = 0;

        // Поиск периода Пизано
        do {
            Third = (First + Second) % m;
            First = Second % m;
            Second = Third % m;
            period += 1;
        }while(First != 0 || Second != 1);

        // Находим остаток от деления n-ого числа Фибоначчи на m
        First = 0;
        Second = 1;
        for(int i = 0; i < n % period; i++){
            Third = (First + Second) % m;
            First = Second % m;
            Second = Third % m;
        }
        return First;
    }


}

