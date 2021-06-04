package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderedPairList<K, V> extends PairList<K, V> {
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
     * Instantiates the backing ArrayList with some initial size.
     *
     * @param size Some initial size of the PairList.
     */
    public OrderedPairList(int size) {
        STORED = new ArrayList<>(size);
    }

    /**
     * Instantiates the backing ArrayList with a predefined initial capacity.
     *
     * @see PairList#INIT_CAPACITY
     */
    public OrderedPairList() {
        STORED = new ArrayList<>(INIT_CAPACITY);
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
}
