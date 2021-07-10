package Kernel.Data_Structures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

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
public abstract class PairCollection<K, V> implements Iterable<Pair<K, V>>, Serializable {
    /**
     * States the default initial capacity for all PairCollection implementations.
     *
     * @implNote Default initial capacity can be changed in accordance with what is necessary for the implementation.
     */
    protected static final byte INIT_CAPACITY = 10; //initial capacity

    /**
     * Backing List.
     *
     * @implNote See specific PairCollection for List dependency information.
     */
    protected List<Pair<K, V>> STORED; //backing arraylist.

    /**
     * Finds a Pair given the target value.
     *
     * @param value Given some object signifying the value.
     * @return first pair when found or Pair with null values when nothing is found.
     */
    public abstract Pair<K, V> getPairFromValue(V value);

    /**
     * Finds a Pair given the target key.
     *
     * @param key Given some object signifying the key.
     * @return first pair when found or Pair with null values when nothing is found.
     */
    public abstract Pair<K, V> getPairFromKey(K key);

    /**
     * Adds a value to the PairCollection.
     *
     * @param key   Given some object signifying the key.
     * @param value Given some object signifying the value.
     * @return boolean if the add was successful.
     */
    public abstract boolean add(K key, V value);

    /**
     * Removes a value from the PairCollection.
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
     * Returns length of the PairCollection.
     *
     * @return integer signifying length.
     */
    public abstract int size();

    /**
     * Returns if collection has key given.
     *
     * @return boolean if collection has key
     */
    public abstract boolean hasKey(K key);

    /**
     * Returns if collection has value given.
     *
     * @return boolean if collection has value
     */
    public abstract boolean hasValue(V value);

    /**
     * Fetches the pair from the list.
     *
     * @param index Any object value
     * @return Pair if found, null if not
     */
    public abstract Pair<K, V> get(int index);

    public final void destroy() {
        STORED = null;
    }
}
