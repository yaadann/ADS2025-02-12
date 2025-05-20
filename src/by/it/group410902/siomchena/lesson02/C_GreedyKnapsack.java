package by.it.group410902.siomchena.lesson02;
/*
Даны
1) объем рюкзака 4
2) число возможных предметов 60
3) сам набор предметов
    100 50
    120 30
    100 50
Все это указано в файле (by/it/a_khmelev/lesson02/greedyKnapsack.txt)

Необходимо собрать наиболее дорогой вариант рюкзака для этого объема
Предметы можно резать на кусочки (т.е. алгоритм будет жадным)
 */

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class C_GreedyKnapsack {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = C_GreedyKnapsack.class.getResourceAsStream("greedyKnapsack.txt");
        double costFinal = new C_GreedyKnapsack().calc(inputStream);
        long finishTime = System.currentTimeMillis();
        System.out.printf("Общая стоимость %f (время %d)", costFinal, finishTime - startTime);
    }

    double calc(InputStream inputStream) throws FileNotFoundException {
        Scanner input = new Scanner(inputStream);
        int n = input.nextInt();      // сколько предметов в файле
        int W = input.nextInt();      // какой вес у рюкзака
        Item[] items = new Item[n];   // получим список предметов

        for (int i = 0; i < n; i++) {
            items[i] = new Item(input.nextInt(), input.nextInt());
        }

        for (Item item : items) {
            System.out.println(item);
        }

        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);

        Arrays.sort(items, (o1, o2) -> Double.compare(o2.getCostPerWeight(), o1.getCostPerWeight()));

        double result = 0;  // Общая стоимость набора предметов

        for (Item item : items) {
            if (W == 0) break;
            if (item.weight <= W) {
                // Берём весь предмет
                result += item.cost;
                W -= item.weight;
                System.out.println("Берём полностью: " + item);
            } else {
                double fraction = (double) W / item.weight;
                result += item.cost * fraction;
                System.out.println("Берём " + (fraction * 100) + "% от предмета: " + item);
                W = 0;
            }
        }

        System.out.printf("Удалось собрать рюкзак на сумму %f\n", result);
        return result;
    }

    private static class Item implements Comparable<Item> {
        int cost;
        int weight;

        Item(int cost, int weight) {
            this.cost = cost;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "cost=" + cost +
                    ", weight=" + weight +
                    '}';
        }

        // Удельная стоимость
        double getCostPerWeight() {
            return (double) cost / weight;
        }

        @Override
        public int compareTo(Item o) {
            // Сравниваем по убыванию удельной стоимости
            return Double.compare(o.getCostPerWeight(), this.getCostPerWeight());
        }
    }
}