package by.it.group410902.jalilova.lesson11;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {
    class Node<E> {
        E data;             // сам элемент (значение)
        Node<E> previous;   // ссылка на предыдущий элемент в общем списке
        Node<E> next;       // ссылка на следующий элемент в общем списке
        Node<E> nextInSet;  // ссылка на следующий элемент в цепочке (если коллизия)

        public Node(E data) {
            this.data = data;
        }
    }
    // вспомогательный внутренний класс — двусвязный список
    class List<E> {
        Node<E> head;  // первый элемент списка
        Node<E> tail;  // последний элемент списка

        // добавление элемента в конец списка
        void add(Node<E> node) {
            if (head == null) {        // если список пуст
                head = node;           // делаем этот элемент первым
            } else {                   // иначе добавляем в конец
                tail.next = node;
                node.previous = tail;
            }
            tail = node;               // обновляем ссылку на хвост
        }

        // удаление узла из списка
        void remove(Node<E> node) {
            // если у удаляемого есть предыдущий, связываем его с next
            if (node.previous != null) {
                node.previous.next = node.next;
            } else {
                head = head.next;  // если удаляем первый элемент
            }

            // если есть следующий, связываем его с предыдущим
            if (node.next != null) {
                node.next.previous = node.previous;
            } else {
                tail = tail.previous;  // если удаляем последний элемент
            }
        }

        // полная очистка списка
        void clear() {
            head = null;
            tail = null;
        }
    }
    //массив "ящиков", каждый индекс хранит начало цепочки узлов с одинаковым хешем
    Node<E>[] elements;
    // начальный размер массива (16)
    static final int INITIAL_SIZE = 16;
    int size; // текущее количество элементов в множестве
    // список для хранения порядка добавления (используется в toString)
    List<E> list = new List<E>();


    // конструктор по умолчанию
    public MyHashSet() {
        this(INITIAL_SIZE);
    }

    // конструктор с заданным размером
    public MyHashSet(int size) {
        elements = new Node[size];
    }

    // преобразование множества в строку, как в стандартных коллекциях
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = list.head;

        // добавляем первый элемент без запятой
        if (current != null) {
            sb.append(current.data);
            current = current.next;
        }

        // остальные элементы с ", "
        while (current != null) {
            sb.append(", ");
            sb.append(current.data);
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }

    // возвращает количество элементов в множестве
    @Override
    public int size() {
        return size;
    }

    // полностью очищает множество
    @Override
    public void clear() {
        Arrays.fill(elements, null);  // очищаем массив бакетов
        size = 0;
        list.clear();                 // очищаем список элементов
    }

    // проверяет, пусто ли множество
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // вычисляем индекс в массиве для данного элемента
    private int getHashCode(E element) {
        // берем хеш-код элемента и делим по модулю длины массива
        return element.hashCode() % elements.length;
    }

    // добавление элемента в множество
    @Override
    public boolean add(E e) {
        int index = getHashCode(e);          // вычисляем индекс
        Node<E> current = elements[index];   // начало цепочки

        // проверяем, есть ли уже такой элемент (чтобы не было дубликатов)
        while (current != null) {
            if (current.data.equals(e)) {
                return false;  // элемент уже есть
            }
            current = current.nextInSet;
        }

        // создаём новый узел
        Node<E> newNode = new Node<E>(e);

        // вставляем его в начало цепочки по этому индексу
        newNode.nextInSet = elements[index];
        elements[index] = newNode;

        // добавляем в общий список (для toString)
        list.add(newNode);

        size++;

        // если множество переполнено (>75%), увеличиваем массив в 2 раза
        if (size > elements.length * 0.75) {
            Node<E>[] newElements = new Node[elements.length * 2];
            current = list.head;
            // перехешируем все элементы в новый массив
            while (current != null) {
                int newIndex = current.data.hashCode() % newElements.length;
                current.nextInSet = newElements[newIndex];
                newElements[newIndex] = current;
                current = current.next;
            }
            elements = newElements;
        }

        return true;
    }

    // удаление элемента из множества
    @Override
    public boolean remove(Object o) {
        E e = (E)o;
        int index = getHashCode(e);  // ищем ячейку
        Node<E> previous = null;
        Node<E> current = elements[index];

        // ищем элемент в цепочке
        while (current != null) {
            if (current.data.equals(e)) {
                // удаляем из цепочки
                if (previous == null) {
                    elements[index] = current.nextInSet;
                } else {
                    previous.nextInSet = current.nextInSet;
                }

                // удаляем из общего списка
                list.remove(current);
                size--;
                return true;
            }
            previous = current;
            current = current.nextInSet;
        }

        return false; // если не нашли
    }

    // проверка, содержится ли элемент в множестве
    @Override
    public boolean contains(Object o) {
        E e = (E)o;
        int index = getHashCode(e);
        Node<E> current = elements[index];

        // ищем в цепочке
        while (current != null) {
            if (current.data.equals(e)) {
                return true;
            }
            current = current.nextInSet;
        }

        return false;
    }

    // проверка: содержатся ли все элементы коллекции в нашем множестве
    @Override
    public boolean containsAll(Collection<?> c) {
        for (var object : c) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    // добавление всех элементов другой коллекции
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (var element : c) {
            if (add(element)) {
                result = true;
            }
        }
        return result;
    }

    // удаление всех элементов из коллекции c, если они есть в нашем множестве
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (var object : c) {
            if (remove(object)) {
                result = true;
            }
        }
        return result;
    }

    // оставляем только те элементы, которые есть в коллекции c
    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.isEmpty()) {
            this.clear();
            return true;
        }

        boolean isModified = false;
        MyHashSet<E> tempSet = new MyHashSet<>(elements.length);
        Node<E> current = list.head;

        // добавляем только те, что есть в переданной коллекции
        while (current != null) {
            if (c.contains(current.data)) {
                tempSet.add(current.data);
                isModified = true;
            }
            current = current.next;
        }

        // заменяем текущее содержимое на новое
        elements = tempSet.elements;
        list.head = tempSet.list.head;
        list.tail = tempSet.list.tail;
        size = tempSet.size;

        return isModified;
    }

    @Override
    public Iterator<E> iterator() { return null; } // пока не реализован

    @Override
    public Object[] toArray() { return new Object[0]; }

    @Override
    public <T> T[] toArray(T[] a) { return null; }
}