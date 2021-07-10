package Kernel.Data_Structures;

import FunctionLibrary.Library.QuickMath;
import FunctionLibrary.Native;

public final class HashList {
    private final static int default_size = 10; //default array size
    public final int length; //length of the array
    private final int m_prime; //max prime value
    private final Entry[] array; //bucket storage
    //total memory left

    public HashList(int length) {
        this.array = new Entry[length];
        this.length = length;
        int prime = 0;
        length = ((length & 1) == 0) ? length - 1 : length;
        itr:
        for (int i = length; i >= 0; i -= 2) {
            int sqrt = (int) QuickMath.Q_sqrt(i); //accuracy
            for (int j = 2; j < sqrt; j++) {
                if (i % j == 0) continue itr;
            }
        }
        this.m_prime = prime;
    }

    public HashList() {
        this.array = new Entry[default_size];
        this.length = default_size;
        this.m_prime = 7;
    }

    public static int hash(Object key, Object value, int m_prime, int length) {
        final int hash = 31 * (key.hashCode() ^ value.hashCode());
        int sum = Native.GenerateHashCode(key.getClass()) + Native.GenerateHashCode(value.getClass());
        return m_prime - (((sum ^ hash) & (hash >>> 8) % length) % m_prime);
    }

    public void add(Object key, Object value) {
        int index = hash(key, value, m_prime, length);
        if (array[index] == null) {
            array[index] = new Entry(key, value);
        } else {
            array[index].add(key, value); //if the slot found is not empty we gotta use a linked list scheme
        }
    }

    static final class Entry {
        Object KEY; //ref to 'key'
        Object VALUE; //ref to 'value'
        Entry next; //ref to next

        public Entry(Object key, Object value) {
            this(key, value, null);
        }

        public Entry(Object KEY, Object VALUE, Entry next) {
            this.VALUE = VALUE;
            this.KEY = KEY;
            this.next = next;
        }

        //forward linked list
        public void add(Object key, Object value) {
            Entry e = new Entry(key, value);
            Entry caboose = this.next;
            while (caboose.next != null) {
                caboose = caboose.next;
            }
            caboose.next = e;
        }
    }
}
