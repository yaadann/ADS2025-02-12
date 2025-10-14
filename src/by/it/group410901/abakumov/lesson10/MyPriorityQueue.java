package by.it.group410901.abakumov.lesson10;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

@SuppressWarnings("unchecked")
public class MyPriorityQueue<E extends Comparable<E>> implements Queue<E> {
    private E[] heap;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MyPriorityQueue() {
        heap = (E[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    //================ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===================

    private void ensureCapacity() {
        if (size >= heap.length) {
            E[] newHeap = (E[]) new Comparable[heap.length * 2];
            for (int i = 0; i < size; i++) newHeap[i] = heap[i];
            heap = newHeap;
        }
    }

    private void swap(int i, int j) {
        E tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = index * 2 + 2;
            int smallest = index;

            if (left < size && heap[left].compareTo(heap[smallest]) < 0) smallest = left;
            if (right < size && heap[right].compareTo(heap[smallest]) < 0) smallest = right;

            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    //================= ОСНОВНЫЕ МЕТОДЫ ======================

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E element) {
        if (element == null) throw new NullPointerException();
        ensureCapacity();
        heap[size] = element;
        heapifyUp(size);
        size++;
        return true;
    }

    @Override
    public boolean offer(E element) {
        return add(element);
    }

    @Override
    public E remove() {
        if (isEmpty()) throw new NoSuchElementException();
        return poll();
    }

    @Override
    public E poll() {
        if (isEmpty()) return null;
        E result = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) heapifyDown(0);
        return result;
    }

    @Override
    public E element() {
        if (isEmpty()) throw new NoSuchElementException();
        return heap[0];
    }

    @Override
    public E peek() {
        return isEmpty() ? null : heap[0];
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) heap[i] = null;
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (heap[i].equals(o)) return true;
        }
        return false;
    }

    //================= КОЛЛЕКЦИОННЫЕ МЕТОДЫ ====================

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            add(e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (!c.contains(heap[i])) {
                heap[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }
        for (int i = newSize; i < size; i++) heap[i] = null;
        size = newSize;

        // Восстановим свойство кучи
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapifyDown(i);
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        int newSize = 0;
        for (int i = 0; i < size; i++) {
            if (c.contains(heap[i])) {
                heap[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }
        for (int i = newSize; i < size; i++) heap[i] = null;
        size = newSize;

        // Перестраиваем кучу, как делает PriorityQueue
        for (int i = size / 2 - 1; i >= 0; i--) {
            heapifyDown(i);
        }

        return modified;
    }

    //================= СЛУЖЕБНЫЕ ====================

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        for (int i = 0; i < size; i++) arr[i] = heap[i];
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }
}

