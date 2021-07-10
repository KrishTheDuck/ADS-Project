package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderedPairList<K, V> extends PairCollection<K, V> {
    private final List<Pair<K, V>> STORED;

    /**
     * Constructor instantiating the backing ArrayList and adds the passed values as a pair.
     *
     * @param key   Any type-matching object signifying the key.
     * @param value Any type-matching object signifying the value.
     * @see OrderedPairList#OrderedPairList(Object, Object, int)
     */
    public OrderedPairList(K key, V value) {
        this(key, value, INIT_CAPACITY);
    }

    /**
     * Constructor instantiating the backing ArrayList and adds the passed values as a pair.
     *
     * @param key   Any type-matching object signifying the key.
     * @param value Any type-matching object signifying the value.
     */
    public OrderedPairList(K key, V value, int size) {
        STORED = new ArrayList<>(size);
        STORED.add(new Pair<>(key, value));
    }


    /**
     * Instantiates the backing ArrayList with some initial length.
     *
     * @param size Some initial length of the PairCollection.
     */
    public OrderedPairList(int size) {
        STORED = new ArrayList<>(size);
    }

    /**
     * Instantiates the backing ArrayList with a predefined initial capacity.
     *
     * @see PairCollection#INIT_CAPACITY
     */
    public OrderedPairList() {
        STORED = new ArrayList<>(INIT_CAPACITY);
    }

    @Override
    public Pair<K, V> getPairFromValue(V value) {
        for (Pair<K, V> p : STORED)
            if (p.matchesValue(value))
                return p;
        return new Pair<>(null, null);
    }

    @Override
    public Pair<K, V> getPairFromKey(K key) {
        for (Pair<K, V> p : STORED)
            if (p.matchesKey(key))
                return p;
        return new Pair<>(null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(K key, V value) {
        STORED.add(new Pair<>(key, value));
        return false;
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return STORED.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Pair<K, V>> iterator() {
        return STORED.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return STORED.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasKey(K key) {
        for (Pair<K, V> pair : STORED) {
            if (pair.matchesKey(key)) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasValue(V value) {
        for (Pair<K, V> pair : STORED) {
            if (pair.matchesValue(value)) return true;
        }
        return false;
    }

    @Override
    public Pair<K, V> get(int index) {
        return STORED.get(index);
    }
}
