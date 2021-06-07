package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A PairCollection implementation that maintains sorted order through insertion.
 *
 * @param <K> Any comparable object
 * @param <V> Any comparable object
 * @author Krish Sridhar♦
 * @implNote Worst-Case insertion is O(n)
 * @see PairCollection
 * @see Pair
 * @see ArrayList
 * @since 1.0
 * Date: May 30, 2021
 */
public class SortedPairList<K extends Comparable<K>, V extends Comparable<V>> extends PairCollection<K, V> {
    private final List<Pair<K, V>> STORED; //backing arraylist.

    /**
     * Constructor instantiating the backing ArrayList and adds the passed values as a pair.
     *
     * @param key   Any type-matching object signifying the key.
     * @param value Any type-matching object signifying the value.
     * @see SortedPairList#SortedPairList(Comparable, Comparable, int)
     */
    public SortedPairList(K key, V value) {
        this(key, value, INIT_CAPACITY);
    }

    /**
     * Constructor instantiating the backing ArrayList and adds the passed values as a pair.
     *
     * @param key   Any type-matching object signifying the key.
     * @param value Any type-matching object signifying the value.
     */
    public SortedPairList(K key, V value, int size) {
        STORED = new ArrayList<>(size);
        STORED.add(new Pair<>(key, value));
    }

    /**
     * Instantiates the backing ArrayList with some initial size.
     *
     * @param size Some initial size of the PairCollection.
     */
    public SortedPairList(int size) {
        STORED = new ArrayList<>(size);
    }

    /**
     * Instantiates the backing ArrayList with a predefined initial capacity.
     *
     * @see PairCollection#INIT_CAPACITY
     */
    public SortedPairList() {
        STORED = new ArrayList<>(INIT_CAPACITY);
    }

    @Override
    public Pair<K, V> getPairFromValue(V value) {
        return b_search(size(), 0, null, value);
    }

    @Override
    public Pair<K, V> getPairFromKey(K key) {
        return b_search(size(), 0, key, null);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote To maintain insertion order the pair is inserted into the list with least to greatest order.
     */
    @Override
    public boolean add(K key, V value) {
        Pair<K, V> insert = new Pair<>(key, value);
        System.out.println("inserting pair: " + insert);
        if (size() == 0) return STORED.add(insert);
        try {
            final int max = size() - 1;
            for (int i = max; i >= 0; i--) {
                if (STORED.get(i).key().compareTo(insert.key()) <= 0) {
                    STORED.add(i + 1, insert);
                    return true;
                }
            }
            STORED.add(0, insert);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote O(log n) time as specified by a binary search of all elements.
     */
    @Override
    public boolean remove(K key, V value) {
        int min = 0;
        int max = size() - 1;

        if (STORED.get(min).equals(key, value)) {
            STORED.remove(min);
            return true;
        }

        if (STORED.get(max).equals(key, value)) {
            STORED.remove(max);
            return true;
        }

        Pair<K, V> target;
        return (target = b_search(max, min, key, value)) != null && STORED.remove(target);
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

    @Override
    public boolean hasKey(K key) {
        return b_search(STORED.size(), 0, key, null) != null;
    }

    public boolean hasValue(V value) {
        return false;
    }

    @Override
    public Pair<K, V> get(int index) {
        return STORED.get(index);
    }

    private Pair<K, V> b_search(int max, int min, K key, V value) {
        int mid = (min + max) / 2;
        Pair<K, V> target = new Pair<>(key, value);

        if (value != null && key != null) {
            while (mid != min && mid != max) {
                if (STORED.get(mid).key().compareTo(key) > 0) { //curr is greater than target
                    max = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).key().compareTo(key) < 0) { //curr is less than target
                    min = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).equals(target)) { //equality reached
                    return STORED.get(mid);
                }
            }
        } else if (value == null) {
            while (mid != min && mid != max) {
                if (STORED.get(mid).key().compareTo(key) > 0) { //curr is greater than target
                    max = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).key().compareTo(key) < 0) { //curr is less than target
                    min = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).key().equals(key)) { //equality reached
                    return STORED.get(mid);
                }
            }
        } else {
            while (mid != min && mid != max) {
                if (STORED.get(mid).value().compareTo(value) > 0) { //curr is greater than target
                    max = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).value().compareTo(value) < 0) { //curr is less than target
                    min = mid;
                    mid = (max + min) / 2;
                } else if (STORED.get(mid).value().equals(value)) { //equality reached
                    return STORED.get(mid);
                }
            }
        }
        return null;
    }
}
