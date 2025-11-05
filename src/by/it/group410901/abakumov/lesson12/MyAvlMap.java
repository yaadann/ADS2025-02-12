package by.it.group410901.abakumov.lesson12;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

public class MyAvlMap implements Map<Integer, String> {
    // Внутренний класс — узел АВЛ-дерева.
    // Хранит ключ, значение, ссылки на левого/правого потомков и высоту.
    private static final class Node {
        Integer key;
        String value;
        Node left, right;
        int height;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root; // корень дерева
    private int size = 0; // количество элементов в карте

    // ---------------------- Вспомогательные методы АВЛ ----------------------

    // Возвращает высоту узла (0, если узел null)
    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    // Пересчитывает высоту узла
    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    // Баланс-фактор (разница высот левого и правого поддерева)
    private int balanceFactor(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    // Правый поворот
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    // Левый поворот
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Балансировка узла после вставки/удаления
    private Node balance(Node node) {
        updateHeight(node);
        int bf = balanceFactor(node);
        if (bf > 1) { // левое поддерево выше правого
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left); // большой левый поворот
            }
            return rotateRight(node);
        }
        if (bf < -1) { // правое поддерево выше левого
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right); // большой правый поворот
            }
            return rotateLeft(node);
        }
        return node;
    }

    // ---------------------- Основные методы Map ----------------------

    // Добавляет пару (key, value) в дерево.
    // Возвращает предыдущее значение, если ключ уже был в дереве.
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException("null keys not supported");
        Holder prev = new Holder();
        root = insert(root, key, value, prev);
        if (prev.found == null) size++; // если ключ новый — увеличиваем размер
        return prev.found;
    }

    // Класс для передачи предыдущего значения при рекурсии
    private static final class Holder {
        String found = null; // хранит старое значение, если найдено
    }

    // Рекурсивная вставка с балансировкой
    private Node insert(Node node, Integer key, String value, Holder prev) {
        if (node == null) {
            return new Node(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) { // ключ уже существует
            prev.found = node.value;
            node.value = value;
            return node;
        } else if (cmp < 0) {
            node.left = insert(node.left, key, value, prev);
        } else {
            node.right = insert(node.right, key, value, prev);
        }
        return balance(node);
    }

    // Удаляет элемент по ключу.
    // Возвращает старое значение или null, если ключ отсутствует.
    @Override
    public String remove(Object keyObj) {
        if (!(keyObj instanceof Integer)) return null;
        Integer key = (Integer) keyObj;
        Holder prev = new Holder();
        root = remove(root, key, prev);
        if (prev.found != null) size--; // уменьшить размер, если удалён элемент
        return prev.found;
    }

    // Рекурсивное удаление с балансировкой
    private Node remove(Node node, Integer key, Holder prev) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key, prev);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, prev);
        } else {
            // найден нужный узел
            prev.found = node.value;
            if (node.left == null || node.right == null) {
                // один или ноль потомков — просто заменить потомком
                Node tmp = (node.left != null) ? node.left : node.right;
                node = tmp;
            } else {
                // два потомка — ищем минимальный в правом поддереве
                Node min = minNode(node.right);
                node.key = min.key;
                node.value = min.value;
                node.right = remove(node.right, min.key, new Holder());
            }
        }
        if (node != null) node = balance(node);
        return node;
    }

    // Возвращает узел с минимальным ключом
    private Node minNode(Node n) {
        Node cur = n;
        while (cur.left != null) cur = cur.left;
        return cur;
    }

    // Возвращает значение по ключу или null, если ключ отсутствует
    @Override
    public String get(Object keyObj) {
        if (!(keyObj instanceof Integer)) return null;
        Integer key = (Integer) keyObj;
        Node cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.value;
            cur = cmp < 0 ? cur.left : cur.right;
        }
        return null;
    }

    // Проверяет наличие ключа
    @Override
    public boolean containsKey(Object keyObj) {
        return get(keyObj) != null;
    }

    // Возвращает количество элементов
    @Override
    public int size() {
        return size;
    }

    // Удаляет все элементы
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Проверяет, пусто ли дерево
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Возвращает строковое представление карты.
    // Формат: {k1=v1, k2=v2, ...} — порядок по возрастанию ключей.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        InOrderPrinter p = new InOrderPrinter(sb);
        p.print(root);
        sb.append('}');
        return sb.toString();
    }

    // Вспомогательный класс для симметричного обхода дерева
    private static final class InOrderPrinter {
        private final StringBuilder sb;
        private boolean first = true;

        InOrderPrinter(StringBuilder sb) { this.sb = sb; }

        void print(Node n) {
            if (n == null) return;
            print(n.left);
            if (!first) sb.append(", "); // разделитель
            first = false;
            sb.append(n.key).append("=").append(n.value);
            print(n.right);
        }
    }

    // ---------------------- Дополнительные методы ----------------------

    // Проверяет, содержится ли указанное значение
    @Override
    public boolean containsValue(Object value) {
        return containsValueIn(root, value);
    }

    // Рекурсивный поиск значения
    private boolean containsValueIn(Node n, Object value) {
        if (n == null) return false;
        if (value == null) {
            if (n.value == null) return true;
        } else if (value.equals(n.value)) {
            return true;
        }
        return containsValueIn(n.left, value) || containsValueIn(n.right, value);
    }

    // Методы ниже не требуются по заданию — не реализованы.

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException("putAll не поддерживается");
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException("entrySet не поддерживается");
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException("keySet не поддерживается");
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException("values не поддерживается");
    }

}
