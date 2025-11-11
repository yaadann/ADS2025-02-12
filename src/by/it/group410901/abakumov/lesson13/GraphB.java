package by.it.group410901.abakumov.lesson13;
import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // Создаём объект для чтения ввода

        String input = sc.nextLine().trim(); // Считываем строку графа и убираем лишние пробелы
        sc.close(); // Закрываем Scanner, он больше не нужен

        Map<String, List<String>> graph = new TreeMap<>(); // Карта: вершина -> список вершин, куда ведут рёбра (лекс. порядок)
        Map<String, Integer> indegree = new TreeMap<>(); // Карта: вершина -> количество входящих рёбер

        String[] relations = input.split(","); // Разделяем строку на отдельные рёбра по запятой

        for (String rel : relations) { // Перебираем каждое ребро
            String[] parts = rel.trim().split("->"); // Разделяем "a -> b" на левую и правую часть
            String from = parts[0].trim(); // Левая часть — вершина-источник
            String to = parts[1].trim(); // Правая часть — вершина-приёмник

            graph.putIfAbsent(from, new ArrayList<>()); // Если вершины from нет в графе — добавляем
            graph.putIfAbsent(to, new ArrayList<>()); // Если вершины to нет в графе — добавляем

            graph.get(from).add(to); // Добавляем ребро: from -> to

            indegree.put(to, indegree.getOrDefault(to, 0) + 1); // Увеличиваем входящую степень вершины to
            indegree.putIfAbsent(from, indegree.getOrDefault(from, 0)); // Убеждаемся, что вершина from тоже есть в карте
        }

        Queue<String> queue = new PriorityQueue<>(); // Очередь для вершин с нулём входящих рёбер (сортируется по алфавиту)

        for (String v : indegree.keySet()) { // Проходим по всем вершинам
            if (indegree.get(v) == 0) { // Если у вершины нет входящих рёбер
                queue.add(v); // Добавляем её в очередь
            }
        }

        int count = 0; // Счётчик обработанных вершин

        while (!queue.isEmpty()) { // Пока есть вершины, которые можно обработать
            String v = queue.poll(); // Берём вершину с наименьшим алфавитным значением
            count++; // Увеличиваем счётчик обработанных вершин

            for (String neigh : graph.get(v)) { // Перебираем соседей текущей вершины
                indegree.put(neigh, indegree.get(neigh) - 1); // Уменьшаем их количество входящих рёбер
                if (indegree.get(neigh) == 0) { // Если входящих рёбер больше нет
                    queue.add(neigh); // Добавляем соседа в очередь
                }
            }
        }

        boolean hasCycle = (count < indegree.size()); // Если обработано меньше вершин, чем всего — есть цикл

        if (hasCycle) { // Если цикл найден
            System.out.println("yes"); // Выводим "yes"
        } else { // Если цикла нет
            System.out.println("no"); // Выводим "no"
        }
    }
}
