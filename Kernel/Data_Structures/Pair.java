package Kernel.Data_Structures;

public record Pair<K, V>(K key, V value) {
    public boolean equals(Pair<K, V> p) {
        if (p == this) return true;
        if (p == null) return false;
        return this.key.equals(p.key) && this.value.equals(p.value);
    }

    @Override
    public String toString() {
        return "Pair: [" +
                key + ", " +
                value + ']';
    }

    public boolean matchesKey(K key) {
        return this.key.equals(key);
    }

    public boolean matchesValue(V value) {
        return this.value.equals(value);
    }
}