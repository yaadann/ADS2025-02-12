package by.it.group410901.abakumov.lesson10;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Deque;

public class MyLinkedList<E> implements Deque<E> {
    //Узел списка
    private static class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(E item, Node<E> prev, Node<E> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    //Поля списка
    private Node<E> head;
    private Node<E> tail;
    private int size = 0;

    //Конструктор
    public MyLinkedList() {}


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;
        while (current != null) {
            sb.append(current.item);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        return sb.append("]").toString();
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    public E remove(int index) {
        checkIndex(index);
        Node<E> current = getNode(index);
        return unlink(current);
    }

    @Override
    public boolean remove(Object element) {
        Node<E> current = head;
        while (current != null) {
            if ((element == null && current.item == null) ||
                    (element != null && element.equals(current.item))) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    @Override
    public void addFirst(E element) {
        Node<E> newNode = new Node<>(element, null, head);
        if (head != null) head.prev = newNode;
        head = newNode;
        if (tail == null) tail = head;
        size++;
    }

    @Override
    public void addLast(E element) {
        Node<E> newNode = new Node<>(element, tail, null);
        if (tail != null) tail.next = newNode;
        tail = newNode;
        if (head == null) head = tail;
        size++;
    }

    @Override
    public E element() {
        if (head == null) throw new NoSuchElementException();
        return head.item;
    }

    @Override
    public E getFirst() {
        if (head == null) throw new NoSuchElementException();
        return head.item;
    }

    @Override
    public E getLast() {
        if (tail == null) throw new NoSuchElementException();
        return tail.item;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (head == null) return null;
        return unlink(head);
    }

    @Override
    public E pollLast() {
        if (tail == null) return null;
        return unlink(tail);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
    }

    private Node<E> getNode(int index) {
        Node<E> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) current = current.next;
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }
        return current;
    }

    private E unlink(Node<E> node) {
        E element = node.item;
        Node<E> prev = node.prev;
        Node<E> next = node.next;

        if (prev == null) head = next;
        else prev.next = next;

        if (next == null) tail = prev;
        else next.prev = prev;

        size--;
        return element;
    }

    @Override
    public boolean offerFirst(E e) {
        return false;
    }

    @Override
    public boolean offerLast(E e) {
        return false;
    }

    @Override
    public E removeFirst() {
        return null;
    }

    @Override
    public E removeLast() {
        return null;
    }

    @Override
    public E peekFirst() {
        return null;
    }

    @Override
    public E peekLast() {
        return null;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return false;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void push(E e) {

    }

    @Override
    public E pop() {
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }


}
