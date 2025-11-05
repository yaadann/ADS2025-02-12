package by.it.group410901.abakumov.lesson12;

class MySplayMap implements java.util.NavigableMap<Integer, String> {
    // Узел дерева
    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    private Node root;
    private int size;

    // Конструктор
    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Вспомогательный метод для splay-операции
    private void splay(Node x) {
        // // Почему используется цикл while? Потому что нужно поднимать узел x до корня через серию поворотов
        while (x.parent != null) {
            Node parent = x.parent;
            Node grandparent = parent.parent;

            if (grandparent == null) {
                // Zig: один поворот, если родитель - корень
                if (x == parent.left) {
                    rotateRight(parent);
                } else {
                    rotateLeft(parent);
                }
            } else {
                if (x == parent.left) {
                    if (parent == grandparent.left) {
                        // Zig-Zig: два правых поворота
                        rotateRight(grandparent);
                        rotateRight(parent);
                    } else {
                        // Zig-Zag: правый, затем левый поворот
                        rotateRight(parent);
                        rotateLeft(grandparent);
                    }
                } else {
                    if (parent == grandparent.right) {
                        // Zig-Zig: два левых поворота
                        rotateLeft(grandparent);
                        rotateLeft(parent);
                    } else {
                        // Zig-Zag: левый, затем правый поворот
                        rotateLeft(parent);
                        rotateRight(grandparent);
                    }
                }
            }
        }
        root = x;
    }

    // Поворот вправо
    private void rotateRight(Node x) {
        // // Зачем нужен поворот? Для изменения структуры дерева, чтобы поднять левый дочерний узел
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // Поворот влево
    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    // Вставка элемента
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();
        // // Почему создается новый узел, если дерево пустое? Потому что это первый элемент
        if (root == null) {
            root = new Node(key, value);
            size++;
            return null;
        }

        Node current = root;
        Node parent = null;
        // Поиск места для вставки
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Ключ уже существует, обновляем значение
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            }
        }

        // Создаем новый узел
        Node newNode = new Node(key, value);
        newNode.parent = parent;
        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        size++;
        splay(newNode); // // Зачем splay после вставки? Чтобы новый узел стал корнем для ускорения последующих операций
        return null;
    }

    // Получение значения по ключу
    @Override
    public String get(Object key) {
        if (key == null) throw new NullPointerException();
        Node node = findNode((Integer) key);
        return node != null ? node.value : null;
    }

    // Поиск узла по ключу
    private Node findNode(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                splay(current); // // Зачем splay здесь? Чтобы поднять найденный узел к корню
                return current;
            }
        }
        return null;
    }

    // Удаление узла
    @Override
    public String remove(Object key) {
        if (key == null) throw new NullPointerException();
        Node node = findNode((Integer) key);
        if (node == null) return null;

        String value = node.value;
        // // Как происходит удаление? Находим узел, поднимаем его в корень, затем соединяем поддеревья
        if (node.left == null) {
            root = node.right;
            if (root != null) root.parent = null;
        } else {
            Node maxLeft = node.left;
            while (maxLeft.right != null) {
                maxLeft = maxLeft.right;
            }
            splay(maxLeft); // Поднимаем максимальный узел левого поддерева
            maxLeft.right = node.right;
            if (node.right != null) node.right.parent = maxLeft;
            root = maxLeft;
        }
        size--;
        return value;
    }

    // Проверка наличия ключа
    @Override
    public boolean containsKey(Object key) {
        if (key == null) throw new NullPointerException();
        return findNode((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        // Проверяем, что value не null и является String
        if (!(value instanceof String)) {
            return false;
        }
        return containsValue(root, (String) value);
    }

    private boolean containsValue(Node node, String value) {
        if (node == null) return false;
        if (node.value.equals(value)) return true;
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    // Размер карты
    @Override
    public int size() {
        return size;
    }

    // Проверка на пустоту
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Очистка карты
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Получение подкарты до указанного ключа
    @Override
    public java.util.NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        if (toKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        headMap(root, toKey, inclusive, result);
        return result;
    }

    private void headMap(Node node, Integer toKey, boolean inclusive, MySplayMap result) {
        if (node == null) return;
        int cmp = node.key.compareTo(toKey);
        if (cmp < 0 || (inclusive && cmp == 0)) {
            headMap(node.left, toKey, inclusive, result);
            result.put(node.key, node.value);
            headMap(node.right, toKey, inclusive, result);
        } else {
            headMap(node.left, toKey, inclusive, result);
        }
    }

    // Получение подкарты от указанного ключа
    @Override
    public java.util.NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        if (fromKey == null) throw new NullPointerException();
        MySplayMap result = new MySplayMap();
        tailMap(root, fromKey, inclusive, result);
        return result;
    }

    private void tailMap(Node node, Integer fromKey, boolean inclusive, MySplayMap result) {
        if (node == null) return;
        int cmp = node.key.compareTo(fromKey);
        if (cmp > 0 || (inclusive && cmp == 0)) {
            tailMap(node.left, fromKey, inclusive, result);
            result.put(node.key, node.value);
            tailMap(node.right, fromKey, inclusive, result);
        } else {
            tailMap(node.right, fromKey, inclusive, result);
        }
    }

    // Первый ключ
    @Override
    public Integer firstKey() {
        if (root == null) throw new java.util.NoSuchElementException();
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        splay(current); // // Зачем splay? Чтобы оптимизировать доступ к минимальному ключу
        return current.key;
    }

    // Последний ключ
    @Override
    public Integer lastKey() {
        if (root == null) throw new java.util.NoSuchElementException();
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        splay(current);
        return current.key;
    }

    // Ключ строго меньше указанного
    @Override
    public Integer lowerKey(Integer key) {
        if (key == null) throw new NullPointerException();
        Node current = root;
        Node result = null;
        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp < 0) {
                result = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        if (result != null) splay(result);
        return result != null ? result.key : null;
    }

    // Ключ меньше или равен указанному
    @Override
    public Integer floorKey(Integer key) {
        if (key == null) throw new NullPointerException();
        Node current = root;
        Node result = null;
        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp <= 0) {
                result = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        if (result != null) splay(result);
        return result != null ? result.key : null;
    }

    // Ключ больше или равен указанному
    @Override
    public Integer ceilingKey(Integer key) {
        if (key == null) throw new NullPointerException();
        Node current = root;
        Node result = null;
        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp >= 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (result != null) splay(result);
        return result != null ? result.key : null;
    }

    // Ключ строго больше указанного
    @Override
    public Integer higherKey(Integer key) {
        if (key == null) throw new NullPointerException();
        Node current = root;
        Node result = null;
        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp > 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (result != null) splay(result);
        return result != null ? result.key : null;
    }

    // Формирование строки в формате {key1=value1, key2=value2}
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        toString(root, sb);
        if (sb.length() > 1) sb.setLength(sb.length() - 2); // Удаляем последнюю запятую
        sb.append("}");
        return sb.toString();
    }

    private void toString(Node node, StringBuilder sb) {
        if (node == null) return;
        toString(node.left, sb); // // Почему рекурсия по левому поддереву? Для вывода ключей в порядке возрастания
        sb.append(node.key).append("=").append(node.value).append(", ");
        toString(node.right, sb);
    }

    @Override
    public java.util.SortedMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public java.util.SortedMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    // Остальные методы интерфейса пока не реализованы
    @Override public Entry<Integer, String> floorEntry(Integer key) { return null; }
    @Override public Entry<Integer, String> ceilingEntry(Integer key) { return null; }
    @Override public Entry<Integer, String> higherEntry(Integer key) { return null; }
    @Override public Entry<Integer, String> lowerEntry(Integer key) { return null; }
    @Override public java.util.Map.Entry<Integer, String> firstEntry() { return null; }
    @Override public java.util.Map.Entry<Integer, String> lastEntry() { return null; }
    @Override public java.util.Map.Entry<Integer, String> pollFirstEntry() { return null; }
    @Override public java.util.Map.Entry<Integer, String> pollLastEntry() { return null; }
    @Override public java.util.NavigableMap<Integer, String> descendingMap() { return null; }
    @Override public java.util.NavigableSet<Integer> navigableKeySet() { return null; }
    @Override public java.util.NavigableSet<Integer> descendingKeySet() { return null; }
    @Override public java.util.NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) { return null; }
    @Override public java.util.SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) { return null; }
    @Override public java.util.Comparator<? super Integer> comparator() { return null; }
    @Override public java.util.Set<java.util.Map.Entry<Integer, String>> entrySet() { return null; }
    @Override public java.util.Set<Integer> keySet() { return null; }
    @Override public java.util.Collection<String> values() { return null; }
    @Override public void putAll(java.util.Map<? extends Integer, ? extends String> m) {}
}

