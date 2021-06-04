package Kernel.Data_Structures;

import java.util.Iterator;

/**
 * Abstract implementation of PairLists which are lists where each entry is specified by a Pair. Each pair contains a
 * key and a value which can both be accessed.
 *
 * @param <K> any object
 * @param <V> any object
 * @author Krish Sridhar
 * @see Pair
 * @since 1.0
 * Date: May 30, 2021
 */
public abstract class PairList<K, V> implements Iterable<Pair<K, V>> {
    /**
     * States the default initial capacity for all PairList implementations.
     *
     * @implNote Default initial capacity can be changed in accordance with what is necessary for the implementation.
     */
    protected static final byte INIT_CAPACITY = 10; //initial capacity

    /**
     * Finds a Pair given the target key.
     *
     * @param key    Given some object signifying the key.
     * @param STORED Some PairList implementation.
     * @param <K>    any object type K matching the type of PairList and key
     * @param <V>    any object type V matching the type of PairList
     * @return first pair when found or Pair with null values when nothing is found.
     */
    public static <K, V> Pair<K, V> getPairFromKey(K key, PairList<K, V> STORED) {
        for (Pair<K, V> p : STORED)
            if (p.matchesKey(key))
                return p;
        return new Pair<>(null, null);
    }

    /**
     * Finds a Pair given the target value.
     *
     * @param value  Given some object signifying the value.
     * @param STORED Some PairList implementation.
     * @param <K>    any object type K matching the type of PairList
     * @param <V>    any object type V matching the type of PairList and value
     * @return first pair when found or Pair with null values when nothing is found.
     */
    public static <K, V> Pair<K, V> getPairFromValue(V value, PairList<K, V> STORED) {
        for (Pair<K, V> p : STORED)
            if (p.matchesValue(value))
                return p;
        return new Pair<>(null, null);
    }

    /**
     * Adds a value to the PairList.
     *
     * @param key   Given some object signifying the key.
     * @param value Given some object signifying the value.
     * @return boolean if the add was successful.
     */
    public abstract boolean add(K key, V value);

    /**
     * Removes a value from the PairList.
     *
     * @param key   Given some object signifying the key.
     * @param value Given some object signifying the value.
     * @return boolean if the remove was successful.
     */
    public abstract boolean remove(K key, V value);


    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Iterator<Pair<K, V>> iterator();


    /**
     * Returns size of the PairList.
     *
     * @return integer signifying size.
     */
    public abstract int size();
}
