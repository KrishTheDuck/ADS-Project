package Kernel.RuntimeManipulation;

import Kernel.Data_Structures.PairCollection;
import Kernel.Datatypes.Data;
import Kernel.Datatypes.FloatString;
import Kernel.Datatypes.IntString;
import Kernel.Datatypes.RegularString;

import java.util.Map;
import java.util.TreeMap;

/**
 * Controls all variable manipulation during runtime, including reading, writing, altering, adding,  destroying variables. The RVM Class is backed by a
 * {@code HashedSortedPairList<String,Data>}.
 *
 * @author Krish Sridhar
 * @see PairCollection
 * @see Data
 * Date: March 13, 2021
 * @since 1.0
 */
final class RuntimeVariableManipulation {
    private final Map<String, Data> rvm = new TreeMap<>();

    public String toString() {
        return rvm.toString();
    }

    private Data returnByDatatype(String datatype, String value) {
        return switch (datatype) {
            case "int", "long", "short", "byte" -> new IntString(value);
            case "string" -> new RegularString(value);
            case "float", "double" -> new FloatString(value);
            default -> throw new IllegalStateException("Unexpected value: " + datatype);
        };
    }

    public void commit(String name, String datatype, String value, String[] properties) {
        Data data = returnByDatatype(datatype, value);
        data.setProperties(properties);
        rvm.put(name, data);
    }

    public boolean contains(String name) {
        return rvm.containsKey(name);
    }

    public String[] properties(String name) {
        return rvm.get(name).getProperties();
    }

    public String value(String name) {
        return new String(rvm.get(name).data());
    }

    public String setValue(String name, byte[] new_data) {
        Data data = rvm.get(name);
        byte[] prev = data.data();
        rvm.put(name, returnByDatatype(Data.datatype(data), new String(new_data)));
        return new String(prev);
    }

    public String datatype(String name) {
        return Data.datatype(rvm.get(name));
    }
}