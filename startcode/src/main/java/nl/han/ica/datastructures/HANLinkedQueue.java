package nl.han.ica.datastructures;

public class HANLinkedQueue<T> implements IHANQueue<T> {

    protected IHANLinkedList<T> internalList;

    public HANLinkedQueue() {
        internalList = new HANLinkedList<>();
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    @Override
    public boolean isEmpty() {
        return internalList.getSize() == 0;
    }

    @Override
    public void enqueue(T value) {
        internalList.insert(internalList.getSize(), value);
    }

    @Override
    public T dequeue() {
        T value = internalList.getFirst();
        internalList.removeFirst();
        return value;
    }

    @Override
    public T peek() {
        return internalList.getFirst();
    }

    @Override
    public int getSize() {
        return internalList.getSize();
    }

    @Override
    public String toString() {
        return "{" +
                "queued: " + this.getSize() + "," +
                "values: " + internalList.toString() +
                "}";
    }
}
