package Kernel.Datatypes;

/**
 * Marker interface marks it as a viable data-wrapper which can be included in the RVM.
 *
 * @see RuntimeManager
 * Date: June 5, 2021
 */
public abstract class Data implements Comparable<Data> {
    private String[] properties;
    private int length;

    public static String datatype(Data instance) {
        if (instance instanceof IntString) {
            return "int";
        } else if (instance instanceof FloatString) {
            return "float";
        } else if (instance instanceof RegularString) {
            return "string";
        }
        return null;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
        this.length = this.properties.length;
    }

    public abstract byte[] data();

    public int properties_length() {
        return length;
    }

    public abstract String toString();
}
