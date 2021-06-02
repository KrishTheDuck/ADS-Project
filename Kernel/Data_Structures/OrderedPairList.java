package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderedPairList<K, V> extends PairList<K, V> {
    private static final int INIT_CAPACITY = 10;
    private final List<Pair<K, V>> STORED;

    public OrderedPairList(K key, V value) {
        STORED = new ArrayList<>(INIT_CAPACITY);
        STORED.add(new Pair<>(key, value));
    }

    public OrderedPairList(int size) {
        STORED = new ArrayList<>(size);
    }

    public OrderedPairList() {
        STORED = new ArrayList<>(INIT_CAPACITY);
    }

    @Override
    public boolean add(K key, V value) {
        STORED.add(new Pair<>(key, value));
        return false;
    }

    @Override
    public boolean remove(K key, V value) {
        Pair<K, V> p = new Pair<>(key, value);
        for (int i = 0, storedSize = STORED.size(); i < storedSize; i++) {
            Pair<K, V> pair = STORED.get(i);
            if (pair.equals(p))
                return STORED.remove(pair);
        }
        return false;
    }

    @Override
    public String toString() {
        return STORED.toString();
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return STORED.iterator();
    }

    @Override
    public int size() {
        return STORED.size();
    }
}
