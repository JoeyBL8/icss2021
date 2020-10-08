package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {

    private HANLinkedList<T> internalList;

    public HANStack() {
        internalList = new HANLinkedList<>();
    }

    @Override
    public void push(T value) {
        internalList.addFirst(value);
    }

    @Override
    public T pop() {
        T value = internalList.getFirst();
        internalList.removeFirst();
        return value;
    }

    @Override
    public T peek() {
        try {
            return internalList.getFirst();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getSize() {
        return this.internalList.getSize();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }
}
