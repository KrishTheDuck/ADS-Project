package Kernel.Data_Structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SortedPairList<K extends Comparable<K>, V extends Comparable<V>> extends PairList<K, V> {
    private final List<Pair<K, V>> STORED;

    public SortedPairList(K key, V value) {
        STORED = new ArrayList<>(10);
        STORED.add(new Pair<>(key, value));
    }

    public SortedPairList(int size) {
        STORED = new ArrayList<>(size);
    }

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

    @Override
    public boolean remove(K key, V value) {
        Pair<K, V> target = new Pair<>(key, value);
        int min = 0;
        int max = size() - 1;

        if (STORED.get(min).equals(target)) {
            STORED.remove(min);
            return true;
        }

        if (STORED.get(max).equals(target)) {
            STORED.remove(max);
            return true;
        }

        int mid = (min + max) / 2;

        while (mid != min && mid != max) {
            if (STORED.get(mid).key().compareTo(target.key()) > 0) { //curr is greater than target
                max = mid;
                mid = (max + min) / 2;
            } else if (STORED.get(mid).key().compareTo(target.key()) < 0) { //curr is less than target
                min = mid;
                mid = (max + min) / 2;
            } else if (STORED.get(mid).key().equals(target.key())) { //equality reached
                STORED.remove(mid);
                return true;
            }
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
