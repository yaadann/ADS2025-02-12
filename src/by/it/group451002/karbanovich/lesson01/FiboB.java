package by.it.group451002.karbanovich.lesson01;

import java.math.BigInteger;

/*
 * Вам необходимо выполнить способ вычисления чисел Фибоначчи со вспомогательным массивом
 * без ограничений на размер результата (BigInteger)
 */

public class FiboB {

    private long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        //вычисление чисел простым быстрым методом
        FiboB fibo = new FiboB();
        int n = 55555;
        System.out.printf("fastB(%d)=%d \n\t time=%d \n\n", n, fibo.fastB(n), fibo.time());
    }

    private long time() {
        return System.currentTimeMillis() - startTime;
    }

    BigInteger fastB(Integer n) {
        BigInteger[] nums = new BigInteger[n+1];
        nums[0] = BigInteger.ZERO;
        nums[1] = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            nums[i] = nums[i - 1].add(nums[i - 2]);
        }
        return nums[n];
    }

}

