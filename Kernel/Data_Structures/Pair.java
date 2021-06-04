package Kernel.Data_Structures;

/**
 * A pair is an object that contains a key and a value.
 *
 * @param <K> any object
 * @param <V> any object
 * @author Krish Sridhar
 * @since 1.0
 * Date: May 30, 2021
 */
public record Pair<K, V>(K key, V value) {
    /**
     * Checks if the key and values for both pairs are equal.
     *
     * @param p Other pair
     * @return boolean value signifying equality or no equality
     */
    public boolean equals(Pair<K, V> p) {
        if (p == this) return true;
        if (p == null) return false;
        return this.key.equals(p.key) && this.value.equals(p.value);
    }

    /**
     * Returns String form of Pair.
     *
     * @return String form of Pair.
     */
    @Override
    public String toString() {
        return "Pair: [" +
                key + ", " +
                value + ']';
    }

    /**
     * Returns true or false depending on if the keys are equal.
     *
     * @param key Some object
     * @return boolean value signifying if there are matching keys.
     */
    public boolean matchesKey(K key) {
        return this.key.equals(key);
    }


    /**
     * Returns true or false depending on if the values are equal.
     *
     * @param value Some object
     * @return boolean value signifying if there are matching values.
     */
    public boolean matchesValue(V value) {
        return this.value.equals(value);
    }
}