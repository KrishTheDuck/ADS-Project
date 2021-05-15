package Kernel.RuntimeManipulation;

import LanguageExceptions.AlreadyCommittedException;
import LanguageExceptions.NonExistentVariableException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Controls all variable manipulation during runtime, including reading, writing, altering, adding, and destroying variables. The RVM Class is backed by a
 * {@code Hashmap<String,String[]>}.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: March 13, 2021
 */
public final class RuntimeVariableManipulation implements RuntimeManipulation {


    private static final int RVM_PROPERTIES_LENGTH = 3; //varname: [properties, datatype, value]
    private static Map<String, String[]> rvm_map; //name, properties:[extra keyword?, struct/datatype?, if struct parameters?, value]

    private RuntimeVariableManipulation(Map<String, String[]> rvm_map) {
        RuntimeVariableManipulation.rvm_map = rvm_map;
    }

    public RuntimeVariableManipulation() {
        rvm_map = new HashMap<>();
    }

    public static RuntimeVariableManipulation RVM() {
        return new RuntimeVariableManipulation(rvm_map);
    }

    /**
     * Commits a variable, along with its numerous properties, to the RVM map while maintaining insertion order and asserting that the variable has not already been added.
     *
     * @param name       Name of the variable dotted with the function it comes from: {@code [method_name].[varname]}. (controls scope)
     * @param properties Array of properties pertaining to the committing variable such as datatype, value, scope, etc.
     */
    public void commit(String name, String... properties) throws AlreadyCommittedException, NonExistentVariableException {
        if (hasName(name))
            throw new AlreadyCommittedException("Variable \"" + name + "\" has already been committed to RVM with properties " + Arrays.toString(fetch(name)));
        if (properties.length < RVM_PROPERTIES_LENGTH) {
            throw new ArrayIndexOutOfBoundsException("Properties of variable \"" + name + "\" is not the proper length of " + RVM_PROPERTIES_LENGTH + ".");
        }
        rvm_map.put(name, properties);
    }

    public void destroy(String name) {
        if (!hasName(name)) return;

        rvm_map.remove(name);
    }

    public String[] fetch(String name) throws NonExistentVariableException {
        if (!hasName(name))
            throw new NonExistentVariableException("Variable \"" + name + "\" does not exist in RVM.");
        return rvm_map.get(name);
    }

    @Override
    public void set(String property, String name, String value) throws NonExistentVariableException {
        if (hasName(name)) {
            switch (property) {
                case "value" -> rvm_map.get(name)[2] = value;
                case "datatype" -> rvm_map.get(name)[1] = value;
                case "property" -> rvm_map.get(name)[0] = value;
                default -> throw new IllegalArgumentException("Property \"" + property + "\" is not applicable.");
            }
        }
        throw new NonExistentVariableException("Variable \"" + name + "\" has not been committed.");
    }

    @Override
    public String get(String property, String name) throws Exception {
        if (hasName(name)) {
            return switch (property) {
                case "value" -> rvm_map.get(name)[2];
                case "datatype" -> rvm_map.get(name)[1];
                case "property" -> rvm_map.get(name)[0];
                default -> throw new IllegalArgumentException("Property \"" + property + "\" is not applicable.");
            };
        }
        throw new NonExistentVariableException("Variable \"" + name + "\" has not been committed.");
    }

    @Override
    public boolean hasName(String name) {
        return rvm_map.containsKey(name);
    }

    @Override
    public String toString() {
        return rvm_map.toString();
    }
}
