package by.it.group410901.bandarzheuskaya.lesson10;

import java.util.*;

public class MyPriorityQueue<E> implements Queue<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private E[] heap; // массив для хранения элементов в виде двоичной кучи
    private int size;
    private final Comparator<? super E> comparator; // компаратор для сравнения элементов

    // Конструкторы

    // Создает пустую очередь с начальной емкостью и естественным порядком
    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.comparator = null;
    }

    // Создает пустую очередь с компаратором
    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this.heap = (E[]) new Object[DEFAULT_CAPACITY];
        this.comparator = comparator;
    }

    // Создает очередь из коллекции
    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<? extends E> c) {
        this.comparator = null;
        this.heap = (E[]) new Object[Math.max(c.size(), DEFAULT_CAPACITY)];
        addAll(c);
    }

    // Вспомогательные методы для работы с кучей

    // Увеличивает емкость массива при необходимости
    private void ensureCapacity() {
        if (size == heap.length) {
            @SuppressWarnings("unchecked")
            E[] newHeap = (E[]) new Object[heap.length * 2];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    // Сравнивает два элемента с использованием компаратора или естественного порядка
    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable<? super E>) a).compareTo(b);
        }
    }

    // Просеивание вверх - восстанавливает свойства кучи при добавлении элемента
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2; // индекс родительского узла
            if (compare(heap[index], heap[parent]) >= 0) {
                break; // свойство кучи не нарушено
            }
            swap(index, parent); // меняем местами с родителем
            index = parent; // продолжаем проверку с новой позиции
        }
    }

    // Просеивание вниз - восстанавливает свойства кучи при удалении корня
    private void heapifyDown(int index) {
        while (true) {
            int left = 2 * index + 1; // индекс левого потомка
            int right = 2 * index + 2; // индекс правого потомка
            int smallest = index; // предполагаем, что текущий узел наименьший

            // Сравниваем с левым потомком
            if (left < size && compare(heap[left], heap[smallest]) < 0) {
                smallest = left;
            }
            // Сравниваем с правым потомком
            if (right < size && compare(heap[right], heap[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break; // свойство кучи восстановлено
            }
            swap(index, smallest); // меняем местами с наименьшим потомком
            index = smallest; // продолжаем проверку с новой позиции
        }
    }

    // Меняет местами два элемента в куче
    private void swap(int i, int j) {
        E temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    // Основные методы Queue

    @Override
    public String toString() {
        if (size == 0) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() { return size; }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null; // очищаем ссылки для сборщика мусора
        }
        size = 0;
    }

    // Добавляет элемент в очередь
    @Override
    public boolean add(E element) { return offer(element); }

    // Удаляет и возвращает головной элемент
    @Override
    public E remove() {
        if (size == 0) throw new NoSuchElementException();
        return poll();
    }

    @Override
    public boolean contains(Object element) {
        // Линейный поиск по массиву кучи
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, heap[i])) return true;
        }
        return false;
    }

    // Добавляет элемент в очередь
    @Override
    public boolean offer(E element) {
        if (element == null) throw new NullPointerException();
        ensureCapacity(); // проверяем емкость
        heap[size] = element; // добавляем в конец
        heapifyUp(size); // восстанавливаем свойства кучи
        size++;
        return true;
    }

    // Удаляет и возвращает головной элемент
    @Override
    public E poll() {
        if (size == 0) return null;
        return removeAt(0); // удаляем корневой элемент
    }

    // Возвращает головной элемент без удаления
    @Override
    public E peek() { return (size == 0) ? null : heap[0]; }

    // Возвращает головной элемент без удаления
    @Override
    public E element() {
        if (size == 0) throw new NoSuchElementException();
        return heap[0];
    }

    @Override
    public boolean isEmpty() { return size == 0; }

    // Удаляет элемент по индексу и восстанавливает свойства кучи
    private E removeAt(int index) {
        E removed = heap[index]; // сохраняем удаляемый элемент
        heap[index] = heap[size - 1]; // заменяем последним элементом
        heap[size - 1] = null; // очищаем последнюю позицию
        size--;

        if (index < size) {
            heapifyDown(index); // просеиваем вниз
            // Если элемент стал меньше родителя, просеиваем вверх
            if (index > 0 && compare(heap[index], heap[(index - 1) / 2]) < 0) {
                heapifyUp(index);
            }
        }
        return removed;
    }

    // Удаляет первое вхождение указанного элемента
    @Override
    public boolean remove(Object o) {
        // Линейный поиск и удаление
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, heap[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    // Вспомогательный метод для проверки наличия элемента в коллекции
    private boolean containsInCollection(Collection<?> c, Object element) {
        for (Object e : c) {
            if (Objects.equals(e, element)) {
                return true;
            }
        }
        return false;
    }


    // Проверяет, содержатся ли все элементы коллекции в очереди
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) return false;
        }
        return true;
    }

    // Добавляет все элементы коллекции в очередь
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (offer(element)) modified = true;
        }
        return modified;
    }

    // Удаляет все элементы, содержащиеся в указанной коллекции
    @Override
    public boolean removeAll(Collection<?> c) {
        // Оптимизированная версия за O(n*m), но без использования HashSet
        if (c.isEmpty() || size == 0) return false;

        // Строим новую кучу только из элементов, которые нужно оставить
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (!containsInCollection(c, heap[i])) {
                heap[newSize++] = heap[i];
            }
        }

        boolean modified = newSize != size;
        size = newSize;

        // Перестраиваем кучу за O(n)
        if (size > 0) {
            for (int i = (size - 1) / 2; i >= 0; i--) {
                heapifyDown(i);
            }
        }

        return modified;
    }

    // Оставляет только элементы, содержащиеся в указанной коллекции
    @Override
    public boolean retainAll(Collection<?> c) {
        // Оптимизированная версия за O(n*m), но без использования HashSet
        if (size == 0) return false;
        if (c.isEmpty()) {
            clear();
            return true;
        }

        // Строим новую кучу только из элементов, которые нужно оставить
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (containsInCollection(c, heap[i])) {
                heap[newSize++] = heap[i];
            }
        }

        boolean modified = newSize != size;
        size = newSize;

        // Перестраиваем кучу за O(n)
        if (size > 0) {
            for (int i = (size - 1) / 2; i >= 0; i--) {
                heapifyDown(i);
            }
        }

        return modified;
    }

    // Итератор и остальные методы

    // Возвращает итератор для обхода элементов очереди
    @Override
    public Iterator<E> iterator() {
        return new PriorityQueueIterator();
    }

    // Внутренний класс итератора
    private class PriorityQueueIterator implements Iterator<E> {
        private int currentIndex = 0; // текущая позиция итератора
        private int lastReturned = -1; // индекс последнего возвращенного элемента

        @Override
        public boolean hasNext() { return currentIndex < size; }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            lastReturned = currentIndex;
            return heap[currentIndex++];
        }

        @Override
        public void remove() {
            if (lastReturned == -1) throw new IllegalStateException();
            removeAt(lastReturned);
            currentIndex--;
            lastReturned = -1;
        }
    }

    // Возвращает массив, содержащий все элементы очереди
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(heap, 0, result, 0, size);
        return result;
    }

    // Возвращает массив, содержащий все элементы очереди
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        System.arraycopy(heap, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    // Сравнивает эту очередь с другой очередью
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Queue)) return false;
        Queue<?> other = (Queue<?>) o;
        if (size != other.size()) return false;

        // Создаем копии для сравнения
        MyPriorityQueue<E> copy1 = new MyPriorityQueue<>(this);
        Queue<?> copy2 = createCopy(other);

        // Сравниваем элементы в порядке извлечения
        while (!copy1.isEmpty() && !copy2.isEmpty()) {
            if (!Objects.equals(copy1.poll(), copy2.poll())) {
                return false;
            }
        }
        return copy1.isEmpty() && copy2.isEmpty();
    }

    // Создает копию очереди для сравнения
    private Queue<?> createCopy(Queue<?> queue) {
        // Простая реализация создания копии через массив
        Object[] elements = queue.toArray();
        MyPriorityQueue<Object> copy = new MyPriorityQueue<>();
        for (Object element : elements) {
            copy.offer(element);
        }
        return copy;
    }

    // Возвращает хеш-код очереди
    @Override
    public int hashCode() {
        int result = 1;
        for (E element : this) {
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }
        return result;
    }
}