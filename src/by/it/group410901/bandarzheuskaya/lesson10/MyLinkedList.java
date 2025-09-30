package by.it.group410901.bandarzheuskaya.lesson10;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    // Внутренний класс для узла списка
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<E> head; // первый узел в списке
    private Node<E> tail; // последний узел в списке
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // Обязательные методы

    // Преобразование списка в строку - элементы в квадратных скобках через запятую
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;

        // Проходим по всем узлам от начала до конца
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    // Добавление элемента в конец списка
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Удаление элемента по индексу
    public E remove(int index) {
        // Проверяем, что индекс в допустимых пределах
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        // Удаляем из начала, конца или середины в зависимости от индекса
        if (index == 0) {
            return removeFirst();
        } else if (index == size - 1) {
            return removeLast();
        } else {
            // Находим узел по индексу и удаляем его
            Node<E> current = getNode(index);
            E removedData = current.data;

            // Перебрасываем ссылки: предыдущий узел ссылается на следующий, и наоборот
            current.prev.next = current.next;
            current.next.prev = current.prev;
            size--;

            return removedData;
        }
    }

    // Удаление элемента по значению
    @Override
    public boolean remove(Object element) {
        // Разделяем обработку null и не-null элементов
        if (element == null) {
            return removeNullElement();
        } else {
            return removeNonNullElement(element);
        }
    }

    // Удаление null элемента
    private boolean removeNullElement() {
        Node<E> current = head;
        // Ищем первый null элемент в списке
        while (current != null) {
            if (current.data == null) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Удаление не-null элемента
    private boolean removeNonNullElement(Object element) {
        Node<E> current = head;
        // Ищем элемент, используя equals
        while (current != null) {
            if (element.equals(current.data)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Удаление конкретного узла из списка
    private void removeNode(Node<E> node) {
        // Обновляем ссылки соседних узлов
        if (node.prev == null) {
            // Если удаляем голову - новая голова становится следующим узлом
            head = node.next;
        } else {
            // Иначе предыдущий узел ссылается на следующий
            node.prev.next = node.next;
        }

        if (node.next == null) {
            // Если удаляем хвост - новый хвост становится предыдущим узлом
            tail = node.prev;
        } else {
            // Иначе следующий узел ссылается на предыдущий
            node.next.prev = node.prev;
        }

        size--;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element);

        if (size == 0) {
            // Если список пустой - новый узел становится и головой и хвостом
            head = tail = newNode;
        } else {
            // Иначе новый узел становится перед текущей головой
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // Добавление элемента в конец списка
    @Override
    public void addLast(E element) {
        Node<E> newNode = new Node<>(element);

        if (size == 0) {
            // Если список пустой - новый узел становится и головой и хвостом
            head = tail = newNode;
        } else {
            // Иначе новый узел становится после текущего хвоста
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    // Получение первого элемента без удаления
    @Override
    public E element() {
        return getFirst();
    }

    // Получение первого элемента без удаления
    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    // Получение последнего элемента без удаления (для Queue интерфейса)
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }

    // Извлечение первого элемента с удалением (для Queue интерфейса)
    @Override
    public E poll() {
        return pollFirst();
    }

    // Извлечение первого элемента с удалением
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null; // Если список пуст
        }

        E data = head.data;
        if (size == 1) {
            // Если был один элемент - список становится пустым
            head = tail = null;
        } else {
            // Иначе голова становится следующий узел
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    // Извлечение последнего элемента с удалением
    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        E data = tail.data;
        if (size == 1) {
            // Если был один элемент - список становится пустым
            head = tail = null;
        } else {
            // Иначе хвост становится предыдущий узел
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    // Вспомогательные методы

    // Получение узла по индексу (оптимизированный поиск)
    private Node<E> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> current;
        if (index < size / 2) {
            // Если индекс в первой половине - ищем с начала
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Если индекс во второй половине - ищем с конца
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    // Методы интерфейса Deque, которые нужно реализовать (упрощенные версии)

    // Проверка пустоты списка
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Предложение добавить элемент в конец (для Queue интерфейса)
    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    // Предложение добавить элемент в начало
    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    // Предложение добавить элемент в конец
    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    // Просмотр первого элемента без удаления (для Queue интерфейса)
    @Override
    public E peek() {
        return peekFirst();
    }

    // Просмотр первого элемента без удаления
    @Override
    public E peekFirst() {
        return (size == 0) ? null : head.data;
    }

    // Просмотр последнего элемента без удаления
    @Override
    public E peekLast() {
        return (size == 0) ? null : tail.data;
    }

    // Удаление первого элемента (для Queue интерфейса)
    @Override
    public E remove() {
        return removeFirst();
    }

    // Удаление первого элемента (бросает исключение если список пуст)
    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return pollFirst();
    }

    // Удаление первого вхождения элемента
    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    // Удаление последнего элемента (бросает исключение если список пуст)
    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("List is empty");
        }
        return pollLast();
    }

    // Удаление последнего вхождения элемента
    @Override
    public boolean removeLastOccurrence(Object o) {
        // Для простоты, в этой реализации совпадает с removeFirstOccurrence
        return remove(o);
    }

    // Добавление элемента в начало (для Stack интерфейса)
    @Override
    public void push(E e) {
        addFirst(e);
    }

    // Извлечение элемента из начала (для Stack интерфейса)
    @Override
    public E pop() {
        return removeFirst();
    }

    // Не реализованные методы (для упрощения)

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // Очистка списка - обнуляем все ссылки
    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}