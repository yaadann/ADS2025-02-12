package by.it.group410901.abakumov.lesson11;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

@SuppressWarnings("unchecked")
public class MyHashSet<E> implements Set<E> {
    // Константа: начальная емкость хэш-таблицы по умолчанию
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    // Константа: коэффициент загрузки по умолчанию (75% заполнения перед расширением)
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // Массив бакетов (цепочек узлов) для хранения элементов
    private Node<E>[] table;
    // Текущее количество элементов в множестве
    private int size;
    // Коэффициент загрузки, определяющий когда нужно расширять таблицу
    private final float loadFactor;

    // Конструктор с указанием начальной емкости и коэффициента загрузки
    public MyHashSet(int initialCapacity, float loadFactor) {
        // Если емкость <=0, используем дефолт
        if (initialCapacity <= 0) initialCapacity = DEFAULT_INITIAL_CAPACITY;
        // Создаем массив узлов заданной длины
        this.table = (Node<E>[]) new Node[initialCapacity];
        // Если loadFactor <=0, используем дефолт
        this.loadFactor = loadFactor <= 0 ? DEFAULT_LOAD_FACTOR : loadFactor;
        this.size = 0;
    }

    // Конструктор с указанием только начальной емкости (loadFactor дефолт)
    public MyHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    // Конструктор по умолчанию (дефолт емкость и loadFactor)
    public MyHashSet() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    // Внутренний класс: узел цепочки в бакете
    private static final class Node<E> {
        // Значение элемента (неизменяемое)
        final E value;
        // Ссылка на следующий узел в цепочке
        Node<E> next;
        // Конструктор узла
        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    // Вычисляет индекс бакета для объекта в таблице заданной длины
    // Для null возвращает 0, иначе хэш без знака % длина
    private int indexFor(Object o, int length) {
        if (o == null) return 0;
        int h = o.hashCode();
        return (h & 0x7fffffff) % length; // Маскируем знак и берем modulo
    }

    // Проверяет, нужно ли расширять таблицу, и делает если да
    private void ensureCapacity() {
        if (size > table.length * loadFactor) {
            rehash(table.length * 2); // Удваиваем емкость
        }
    }

    // Перехэширование: создает новую таблицу и переносит все элементы
    private void rehash(int newCapacity) {
        Node<E>[] old = table; // Сохраняем старую таблицу
        Node<E>[] nt = (Node<E>[]) new Node[newCapacity]; // Новая таблица
        // Проходим по всем старым бакетам
        for (int i = 0; i < old.length; i++) {
            Node<E> n = old[i]; // Текущий узел в цепочке
            while (n != null) {
                Node<E> next = n.next; // Сохраняем следующий для продолжения
                int idx = indexFor(n.value, newCapacity); // Новый индекс
                n.next = nt[idx]; // Вставляем в начало новой цепочки
                nt[idx] = n;
                n = next; // Переход к следующему
            }
        }
        table = nt; // Обновляем ссылку на таблицу
    }

    /////////////////////////////
    // Обязательные методы
    /////////////////////////////

    // Возвращает количество элементов
    @Override
    public int size() {
        return size;
    }

    // Очищает множество: обнуляет все бакеты и размер
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) table[i] = null;
        size = 0;
    }

    // Проверяет, пустое ли множество
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Проверяет наличие объекта: ищет в цепочке по индексу
    @Override
    public boolean contains(Object o) {
        int idx = indexFor(o, table.length);
        Node<E> n = table[idx];
        while (n != null) {
            if (equalsElement(n.value, o)) return true; // Сравнение с учетом null
            n = n.next;
        }
        return false;
    }

    // Добавляет элемент, если его нет
    @Override
    public boolean add(E e) {
        if (contains(e)) return false; // Уже есть - ничего не делаем
        ensureCapacity(); // Расширяем если нужно
        int idx = indexFor(e, table.length);
        Node<E> n = new Node<>(e, table[idx]); // Новый узел в начало цепочки
        table[idx] = n;
        size++;
        return true;
    }

    // Удаляет объект, если найден
    @Override
    public boolean remove(Object o) {
        int idx = indexFor(o, table.length);
        Node<E> n = table[idx];
        Node<E> prev = null; // Предыдущий узел для перелинковки
        while (n != null) {
            if (equalsElement(n.value, o)) {
                if (prev == null) {
                    table[idx] = n.next; // Удаляем первый в цепочке
                } else {
                    prev.next = n.next; // Пропускаем удаляемый
                }
                size--;
                return true;
            }
            prev = n;
            n = n.next;
        }
        return false;
    }

    // Сравнивает элементы с учетом null (null == null)
    private boolean equalsElement(E a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    /////////////////////////////
    // Вспомогательные / дополнительные
    /////////////////////////////

    // Строковое представление: [elem1, elem2, ...]
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true; // Для запятых
        for (int i = 0; i < table.length; i++) {
            Node<E> n = table[i];
            while (n != null) {
                if (!first) sb.append(", ");
                sb.append(n.value);
                first = false;
                n = n.next;
            }
        }
        sb.append(']');
        return sb.toString();
    }

    /////////////////////////////
    // Методы интерфейса Set/Collection, упрощённые реализации
    /////////////////////////////

    // Возвращает итератор по элементам
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    // Внутренний итератор
    private class MyIterator implements Iterator<E> {
        int bucketIndex = 0; // Текущий индекс бакета
        Node<E> current = null; // Текущий узел
        Node<E> lastReturned = null; // Последний возвращенный для remove
        // Конструктор: переходит к первому элементу
        MyIterator() {
            advanceToNext();
        }
        // Переходит к следующему непустому узлу или бакету
        private void advanceToNext() {
            if (current != null && current.next != null) {
                current = current.next; // Следующий в той же цепочке
                return;
            }
            current = null;
            while (bucketIndex < table.length) {
                if (table[bucketIndex] != null) {
                    current = table[bucketIndex++]; // Берем первый в бакете
                    return;
                }
                bucketIndex++; // Пропускаем пустой
            }
        }
        // Есть ли следующий элемент
        @Override
        public boolean hasNext() {
            return current != null;
        }
        // Возвращает следующий и продвигается
        @Override
        public E next() {
            if (current == null) throw new NoSuchElementException(); // Нет элементов
            lastReturned = current;
            E val = current.value;
            advanceToNext();
            return val;
        }
        // Удаляет последний возвращенный элемент
        @Override
        public void remove() {
            if (lastReturned == null) throw new IllegalStateException(); // Не был next
            MyHashSet.this.remove(lastReturned.value); // Удаляем через метод сета
            lastReturned = null;
        }
    }

    // Возвращает массив всех элементов
    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (int b = 0; b < table.length; b++) {
            Node<E> n = table[b];
            while (n != null) {
                arr[i++] = n.value;
                n = n.next;
            }
        }
        return arr;
    }

    // Возвращает массив в переданный (или новый если мал)
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            // create new array of runtime type
            return (T[]) java.util.Arrays.copyOf(toArray(), size, a.getClass()); // Копируем в новый
        }
        Object[] arr = a;
        int i = 0;
        for (int b = 0; b < table.length; b++) {
            Node<E> n = table[b];
            while (n != null) {
                arr[i++] = n.value;
                n = n.next;
            }
        }
        if (a.length > size) a[size] = null; // Обнуляем остаток
        return a;
    }

    // Содержит ли все элементы из коллекции
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) if (!contains(o)) return false;
        return true;
    }

    // Добавляет все элементы из коллекции (если новых)
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) if (add(e)) changed = true;
        return changed;
    }

    // Оставляет только элементы, присутствующие в c
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (!c.contains(e)) {
                it.remove(); // Удаляем через итератор
                changed = true;
            }
        }
        return changed;
    }

    // Удаляет все элементы из c
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) if (remove(o)) changed = true;
        return changed;
    }
}