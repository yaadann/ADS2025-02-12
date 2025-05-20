package by.it.group451001.volynets.lesson02;
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
import java.util.Scanner;
import java.util.Arrays;

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
        int n = input.nextInt();      //сколько предметов в файле
        int W = input.nextInt();      //какой вес у рюкзака
        Item[] items = new Item[n];   //получим список предметов
        for (int i = 0; i < n; i++) { //создавая каждый конструктором
            items[i] = new Item(input.nextInt(), input.nextInt());
        }
        //покажем предметы
        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);


        double result = 0;

        // Сортировка предметов по удельной стоимости (стоимость/вес) в порядке убывания
        // Используем встроенную сортировку
        Arrays.sort(items);

        // Жадный алгоритм: берем предметы с наибольшей удельной стоимостью,
        // пока не заполним рюкзак
        int remainingWeight = W;

        for (int i = 0; i < n && remainingWeight > 0; i++) {
            Item currentItem = items[i];

            // Если можем взять весь предмет
            if (remainingWeight >= currentItem.weight) {
                result += currentItem.cost;
                remainingWeight -= currentItem.weight;
            } else {
                //тогда берем часть предмета
                double fraction = (double) remainingWeight / currentItem.weight;
                result += currentItem.cost * fraction;
                remainingWeight = 0;
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
        public String toString()
        {
            return "Item{" +
                    "cost=" + cost +
                    ", weight=" + weight +
                    '}';
        }

        @Override
        public int compareTo(Item o) {

            if (weight == 0) {
                return -1;
            }
            if (o.weight == 0) {
                return 1;
            }

            return Double.compare((double) o.cost * weight, (double) cost * o.weight);
        }
    }
}