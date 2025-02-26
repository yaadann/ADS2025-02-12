package by.it.group451001.alexandrovich.lesson01;

/*
 * Даны целые числа 1<=n<=1E18 и 2<=m<=1E5,
 * необходимо найти остаток от деления n-го числа Фибоначчи на m
 * время расчета должно быть не более 2 секунд
 */

import java.math.BigInteger;

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
        int p;
        p = GetPizanoPeriod(m);
        System.out.print(p);
        return -1L;
    }

    int GetPizanoPeriod(int m){
        int num1 = 0;
        int num2 = 1;
        int num = 1;
        for (int i = 1; i<=m;i++) {
            num = num1 + num2;
            num1 = num2;
            num2 = num;
            if (num % m == 0){
                return i;
            }
        }
        return 0;
    }


}

