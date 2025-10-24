package by.it.group410901.bandarzheuskaya.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        // Считываем строку с описанием графа
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        // Парсим строку и строим граф
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, List<String>> reverseGraph = new HashMap<>();

        // Разбиваем на пары "вершина -> список вершин"
        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split("->");
            String from = parts[0].trim();
            String to = parts[1].trim();

            // Добавляем ребро в прямой граф
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            // Добавляем ребро в обратный граф
            reverseGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);

            // Гарантируем, что все вершины есть в обоих графах
            graph.putIfAbsent(to, new ArrayList<>());
            reverseGraph.putIfAbsent(from, new ArrayList<>());
        }

        // Находим компоненты сильной связности с помощью алгоритма Косарайю
        List<List<String>> scc = kosaraju(graph, reverseGraph);

        // Выводим результат
        for (List<String> component : scc) {
            // Сортируем вершины в компоненте в лексикографическом порядке
            Collections.sort(component);
            // Объединяем вершины в одну строку
            StringBuilder sb = new StringBuilder();
            for (String vertex : component) {
                sb.append(vertex);
            }
            System.out.println(sb.toString());
        }
    }

    private static List<List<String>> kosaraju(Map<String, List<String>> graph,
                                               Map<String, List<String>> reverseGraph) {
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();

        // Первый проход DFS для заполнения стека
        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsFirstPass(node, graph, visited, stack);
            }
        }

        // Второй проход DFS по обратному графу
        visited.clear();
        List<List<String>> scc = new ArrayList<>();

        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (!visited.contains(node)) {
                List<String> component = new ArrayList<>();
                dfsSecondPass(node, reverseGraph, visited, component);
                scc.add(component);
            }
        }

        return scc;
    }

    private static void dfsFirstPass(String current, Map<String, List<String>> graph,
                                     Set<String> visited, Stack<String> stack) {
        visited.add(current);

        if (graph.containsKey(current)) {
            for (String neighbor : graph.get(current)) {
                if (!visited.contains(neighbor)) {
                    dfsFirstPass(neighbor, graph, visited, stack);
                }
            }
        }

        stack.push(current);
    }

    private static void dfsSecondPass(String current, Map<String, List<String>> reverseGraph,
                                      Set<String> visited, List<String> component) {
        visited.add(current);
        component.add(current);

        if (reverseGraph.containsKey(current)) {
            for (String neighbor : reverseGraph.get(current)) {
                if (!visited.contains(neighbor)) {
                    dfsSecondPass(neighbor, reverseGraph, visited, component);
                }
            }
        }
    }
}