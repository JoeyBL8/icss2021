package nl.han.ica.datastructures;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private ListNode header = new ListNode(null);

    private int size = 0;

    @Override
    public void addFirst(T value) {
        this.insert(0, value);
    }

    @Override
    public void clear() {
        header.next = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        ListNode toBeAdded = new ListNode(value);
        ListNode previous = getPrevious(index);
        ListNode toAppend = previous.next;
        previous.next = toBeAdded;
        toBeAdded.next = toAppend;
        size++;
    }

    @Override
    public void delete(int pos) {
        ListNode previous = getPrevious(pos);
        if (previous.next == null) throw new IndexOutOfBoundsException(pos);
        previous.next = previous.next.next;
        size--;
    }

    @Override
    public T get(int pos) {
        ListNode previous = getPrevious(pos);
        if (previous.next == null) throw new IndexOutOfBoundsException(pos);
        return previous.next.value;
    }

    @Override
    public void removeFirst() {
        this.delete(0);
    }

    @Override
    public T getFirst() {
        return this.get(0);
    }

    @Override
    public int getSize() {
        return size;
    }

    private ListNode getPrevious(int index) {
        ListNode previous = header;
        for (int i = 0; i < index; i++) {
            if (previous.next == null) throw new IndexOutOfBoundsException(index);
            previous = previous.next;
        }
        return previous;
    }

    @Override
    public Iterator<T> iterator() {
        throw new RuntimeException("iterator is not implemented");
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        ListNode current = header.next;
        while (current != null) {
            action.accept(current.value);
            current = current.next;
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        throw new RuntimeException("spliterator is not implemented");
    }

    private class ListNode {
        T value;

        ListNode next;

        ListNode(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        ListNode current = header.next;
        while(current != null) {
            builder.append(current);
            if (current.next != null) builder.append(",");
            current = current.next;
        }
        builder.append("]");
        return builder.toString();
    }
}
