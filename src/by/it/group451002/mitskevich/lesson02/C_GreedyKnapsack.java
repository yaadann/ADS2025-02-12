package by.it.group451002.mitskevich.lesson02;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class C_GreedyKnapsack {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = C_GreedyKnapsack.class.getResourceAsStream("greedyKnapsack.txt");
        double costFinal = 0;
        try {
            costFinal = new C_GreedyKnapsack().calc(inputStream);
        } catch (Exception e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
        long finishTime = System.currentTimeMillis();
        System.out.printf("Общая стоимость %f (время %d мс)\n", costFinal, finishTime - startTime);
    }

    double calc(InputStream inputStream) {
        Scanner input = new Scanner(inputStream);
        int n = input.nextInt();      // количество предметов
        int W = input.nextInt();      // вместимость рюкзака
        Item[] items = new Item[n];

        for (int i = 0; i < n; i++) {
            items[i] = new Item(input.nextInt(), input.nextInt());
        }

        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);

        // Сортировка предметов по убыванию удельной стоимости
        Arrays.sort(items);

        double result = 0;
        int capacity = W;

        for (Item item : items) {
            if (capacity == 0) break;

            if (item.weight <= capacity) {
                // Берем весь предмет
                result += item.cost;
                capacity -= item.weight;
            } else {
                // Берем только часть
                double fraction = (double) capacity / item.weight;
                result += item.cost * fraction;
                capacity = 0;
            }
        }

        System.out.printf("Удалось собрать рюкзак на сумму %f\n", result);
        return result;
    }

    private static class Item implements Comparable<Item> {
        int cost;
        int weight;
        double valuePerWeight;

        Item(int cost, int weight) {
            this.cost = cost;
            this.weight = weight;
            this.valuePerWeight = (double) cost / weight;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "cost=" + cost +
                    ", weight=" + weight +
                    ", valuePerWeight=" + String.format("%.2f", valuePerWeight) +
                    '}';
        }

        @Override
        public int compareTo(Item o) {
            // Сортируем по убыванию удельной стоимости
            return Double.compare(o.valuePerWeight, this.valuePerWeight);
        }
    }
}
