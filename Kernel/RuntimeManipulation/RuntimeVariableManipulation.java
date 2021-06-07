package Kernel.RuntimeManipulation;

import Kernel.Data_Structures.HashedSortedPairList;
import Kernel.Data_Structures.PairCollection;
import Kernel.Datatypes.Data;
import Kernel.Datatypes.FloatString;
import Kernel.Datatypes.IntString;
import Kernel.Datatypes.RegularString;

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
    private final HashedSortedPairList<String, Data> rvm = new HashedSortedPairList<>();

    public void commit(String name, String datatype, String value, String[] properties) {
        Data data = switch (datatype) {
            case "int", "long", "short", "byte" -> new IntString(value);
            case "string" -> new RegularString(value);
            case "float", "double" -> new FloatString(value);
            default -> throw new IllegalStateException("Unexpected value: " + datatype);
        };
        data.setProperties(properties);
//        rvm.add(name, data);
    }

    public boolean contains(String name) {
        return rvm.hasKey(name);
    }

    public String[] properties(String name) {
        return rvm.getPairFromKey(name).value().getProperties();
    }

    public String value(String name) {
        return new String(rvm.getPairFromKey(name).value().data());
    }

    public String setValue(String name) {
        String s = value(name);
        return "";
    }

    public String datatype(String name) {
        return Data.datatype(rvm.getPairFromKey(name).value());
    }
}