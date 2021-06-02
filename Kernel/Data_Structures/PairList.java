package Kernel.Data_Structures;

import java.util.Iterator;

public abstract class PairList<K, V> implements Iterable<Pair<K, V>> {
    public static <K, V> Pair<K, V> getPairFromKey(K key, PairList<K, V> STORED) {
        for (Pair<K, V> p : STORED)
            if (p.matchesKey(key))
                return p;
        return null;
    }

    public static <K, V> Pair<K, V> getPairFromValue(V value, PairList<K, V> STORED) {
        for (Pair<K, V> p : STORED)
            if (p.matchesValue(value))
                return p;
        return null;
    }

    public abstract boolean add(K key, V value);

    public abstract boolean remove(K key, V value);

    @Override
    public abstract String toString();

    @Override
    public abstract Iterator<Pair<K, V>> iterator();

    public abstract int size();
}
