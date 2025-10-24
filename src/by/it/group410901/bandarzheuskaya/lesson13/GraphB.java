package by.it.group410901.bandarzheuskaya.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        // Считываем строку с описанием графа
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        // Парсим строку и строим граф
        Map<String, List<String>> graph = new HashMap<>();

        // Разбиваем на пары "вершина -> список вершин"
        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0].trim();
            String to = parts[1].trim();

            // Добавляем ребро в граф
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            // Гарантируем, что конечная вершина тоже есть в графе
            graph.putIfAbsent(to, new ArrayList<>());
        }

        // Проверяем наличие циклов
        boolean hasCycle = hasCycle(graph);

        // Выводим результат
        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean hasCycle(Map<String, List<String>> graph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        // Проверяем каждую вершину на наличие циклов
        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                if (dfs(node, graph, visited, recursionStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean dfs(String current, Map<String, List<String>> graph,
                               Set<String> visited, Set<String> recursionStack) {
        // Помечаем вершину как посещенную и добавляем в стек рекурсии
        visited.add(current);
        recursionStack.add(current);

        // Проверяем всех соседей
        if (graph.containsKey(current)) {
            for (String neighbor : graph.get(current)) {
                if (!visited.contains(neighbor)) {
                    // Если сосед не посещен, рекурсивно проверяем его
                    if (dfs(neighbor, graph, visited, recursionStack)) {
                        return true;
                    }
                } else if (recursionStack.contains(neighbor)) {
                    // Если сосед уже в стеке рекурсии - найден цикл
                    return true;
                }
            }
        }

        // Убираем вершину из стека рекурсии перед возвратом
        recursionStack.remove(current);
        return false;
    }
}
