package EOY_ADS_PROJECT.Compiler.Kernel;

import EOY_ADS_PROJECT.LanguageExceptions.AlreadyCommittedException;
import EOY_ADS_PROJECT.LanguageExceptions.NonExistentVariableException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls all variable manipulation during runtime, including reading, writing, altering, adding, and destroying variables. The RVM Class is backed by a
 * {@code Hashmap<String,String[]>}.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: March 13, 2021
 */
public final class RuntimeVariableManipulation {

    /*NOTEME FIXME TODO
                     - yeah pushing structs into the rvm_map may be a bit confusing
     *               perhaps what we could do is dot the struct instance with the struct name it derives from
     *               like <struct name>.<instance name> and have its properties be a keyword, struct, [params], [stored values].
     *                  --------------------------------------------------------------------------------------------------------
     *               - For other variables its simpler. <method name>.<variable name> and its properties:[keyword?, datatype, value]
     *               so storage is simple but interpretation is a whole other construct that is down to the Executor. When we manipulate
     *               values we have to assert that we understand what properties the variable actually has. In essence RVM just stores values,
     *               the actual work is done by the runtime software which interprets the values and instructions the right way
     *                -----------------------------------------------------------------------------------------------------------
     *               - Another idea would be to have a list of struct layouts and everytime we push a struct instance into RVM we can reference the index or the name of the struct
     *               so that if we were to set two structs equal to each other or something we could easily verify the types.*/
    /*NOTEME FIXME TODO
     *         uh... I don't really know how structs will work in this one. */

    private static Map<String, List<String>> rvm_map; //name, properties:[extra keyword?, struct/datatype?, if struct parameters?, value]
    private static Map<String, List<String>> rvm_SLH;

    private static final int RVM_PROPERTIES_LENGTH = 3; //varname: [properties, datatype, value]
    private static final int SLH_PROPERTIES_LENGTH = 2; //struct name: [properties, params];

    private RuntimeVariableManipulation(Map<String, List<String>> rvm_map, Map<String, List<String>> rvm_struct_layout) {
        RuntimeVariableManipulation.rvm_map = rvm_map;
        RuntimeVariableManipulation.rvm_SLH = rvm_struct_layout;
    }

    public RuntimeVariableManipulation() {
        rvm_map = new HashMap<>();
        rvm_SLH = new HashMap<>();
    }

    /**
     * Commits a variable, along with its numerous properties, to the RVM map while maintaining insertion order and asserting that the variable has not already been added.
     * TODO properties of rvm_map must be asserted with order [property,datatype,value/
     *
     * @param scoped_varname Name of the variable dotted with the function it comes from: {@code [method_name].[varname]}. (controls scope)
     * @param properties     Array of properties pertaining to the committing variable such as datatype, value, scope, etc.
     */
    public void commit(String scoped_varname, String... properties) throws AlreadyCommittedException, NonExistentVariableException {
        if (RVMHasVariable(scoped_varname))
            throw new AlreadyCommittedException("Variable \"" + scoped_varname + "\" has already been committed to RVM with properties " + fetch(scoped_varname));
        if (properties.length < RVM_PROPERTIES_LENGTH) {
            throw new ArrayIndexOutOfBoundsException("Properties of variable \"" + scoped_varname + "\" is not the proper length of " + RVM_PROPERTIES_LENGTH + ".");
        }
        rvm_map.put(scoped_varname, Arrays.asList(properties));
    }

    public void commit_SL(String struct_name, String... properties) throws NonExistentVariableException, AlreadyCommittedException {
        if (SLHHasVariable(struct_name)) {
            throw new AlreadyCommittedException("Variable \"" + struct_name + "\" has already been committed to RVM with properties " + fetchSL(struct_name));
        }
        if (properties.length < SLH_PROPERTIES_LENGTH) {
            throw new ArrayIndexOutOfBoundsException("Properties of variable \"" + struct_name + "\" is not the proper length of " + SLH_PROPERTIES_LENGTH + ".");
        }
        rvm_map.put(struct_name, Arrays.asList(properties));
    }

    public void destroy(String scoped_varname) {
        if (!RVMHasVariable(scoped_varname)) return;

        rvm_map.remove(scoped_varname);
    }

    public List<String> fetch(String scoped_varname) throws NonExistentVariableException {
        if (!RVMHasVariable(scoped_varname))
            throw new NonExistentVariableException("Variable \"" + scoped_varname + "\" does not exist in RVM.");
        return rvm_map.get(scoped_varname);
    }

    public List<String> fetchSL(String struct_name) throws NonExistentVariableException {
        if (!SLHHasVariable(struct_name))
            throw new NonExistentVariableException("Variable \"" + struct_name + "\" does not exist in RVM.");
        return rvm_map.get(struct_name);
    }

    public void setValue(String scoped_varname, String value) throws NonExistentVariableException {
        if (!RVMHasVariable(scoped_varname))
            throw new NonExistentVariableException("Variable \"" + scoped_varname + "\" does not exist in RVM.");
        rvm_map.get(scoped_varname).set(2, value);
    }

    public String getValue(String scoped_varname) throws NonExistentVariableException {
        return fetch(scoped_varname).get(2);
    }

    public String getDatatype(String scoped_varname) throws NonExistentVariableException {
        return fetch(scoped_varname).get(1);
    }

    public String getProperty(String scoped_varname) throws NonExistentVariableException {
        return fetch(scoped_varname).get(0);
    }

    public boolean RVMHasVariable(String scoped_varname) {
        return rvm_map.containsKey(scoped_varname);
    }

    public boolean SLHHasVariable(String struct_name) {
        return rvm_SLH.containsKey(struct_name);
    }

    @Override
    public String toString() {
        return rvm_map.toString();
    }

    public static RuntimeVariableManipulation RVM() {
        return new RuntimeVariableManipulation(rvm_map, rvm_SLH);
    }


    public static void main(String[] args) throws AlreadyCommittedException {
    }
}
