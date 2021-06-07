package Kernel.Data_Structures;

import java.util.Iterator;


public class HashedSortedPairList<K extends Comparable<K>, V extends Comparable<V>> extends PairCollection<K, V> {
    public final SortedPairList<Long, Pair<K, V>> STORED;

    public HashedSortedPairList() {
        STORED = new SortedPairList<>(PairCollection.INIT_CAPACITY);
    }

    public HashedSortedPairList(int size) {
        STORED = new SortedPairList<>(size);
    }

    private long hash(Pair<K, V> p) {
        long hash = p.key().hashCode();
        hash += p.value().hashCode();
        hash += p.hashCode();
        return hash % size();
    }

    @Override
    public Pair<K, V> getPairFromValue(V value) {
        return null;
    }

    @Override
    public Pair<K, V> getPairFromKey(K key) {
        return null;
    }

    @Override
    public boolean add(K key, V value) {
        Pair<K, V> target = new Pair<>(key, value);
        return STORED.add(hash(target), target);
    }

    @Override
    public boolean remove(K key, V value) {
        return STORED.remove();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean hasKey(K key) {
        return false;
    }

    @Override
    public boolean hasValue(V value) {
        return false;
    }

    @Override
    public Pair<K, V> get(int index) {
        return null;
    }

}
