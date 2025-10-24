package by.it.group410901.bandarzheuskaya.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        //считываем строку с описанием графа
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        //строим граф
        Map<String, List<String>> graph = new HashMap<>(); //для каждой вершины хранит список вершин, в которые ведут ребра
        Map<String, Integer> inDegree = new HashMap<>();    //для каждой вершины хранит, сколько ребер в нее входит

        // Разбиваем на пары "вершина -> список вершин"
        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0].trim();
            String to = parts[1].trim();

            // Добавляем ребро в граф
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);

            // Инициализируем степени захода для всех вершин
            inDegree.putIfAbsent(from, 0);
            inDegree.putIfAbsent(to, 0);
        }

        // Вычисляем степени захода для всех вершин
        for (List<String> neighbors : graph.values()) { //перебираем  вершины, в которые ведут стрелки из некоторой вершины-источника
            for (String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1); //получаем текущее значение счетчика для вершины neighbor и увеличиваем на 1
            }
        }

        // Топологическая сортировка с использованием алгоритма Кана
        List<String> result = topologicalSort(graph, inDegree);

        // Выводим результат
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(result.get(i));
        }
    }

    private static List<String> topologicalSort(Map<String, List<String>> graph,
                                                Map<String, Integer> inDegree) {
        List<String> result = new ArrayList<>();

        // PriorityQueue для лексикографического порядка при равнозначности
        PriorityQueue<String> queue = new PriorityQueue<>();

        // Добавляем вершины с нулевой степенью захода
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();  //извлекаем вершину из начала очереди (с наивысшим приоритетом)
            result.add(current);

            //уменьшаем степень захода для всех соседей
            if (graph.containsKey(current)) {
                //сортируем соседей для сохранения лексикографического порядка
                List<String> neighbors = graph.get(current);
                Collections.sort(neighbors);

                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return result;
    }
}