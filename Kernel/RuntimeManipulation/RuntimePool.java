package Kernel.RuntimeManipulation;

/**
 * Controls pool of major runtime resources.
 *
 * @author Krish Sridhar
 * @implNote Anytime a class is created that must request data from the RVM must first go through the RuntimePool.
 * @see RuntimeVariableManipulation
 * @since 1.0
 * Date: 6/6/2021
 */
public class RuntimePool {
    public static final RuntimeVariableManipulation __RVM__ = new RuntimeVariableManipulation();

    //-------------------------------------------------------------
    //RVM CODE
    //-------------------------------------------------------------
    public static void commit(String name, String datatype, String value, String[] properties) {
        __RVM__.commit(name, datatype, value, properties);
    }

    public static boolean contains(String name) {
        return __RVM__.contains(name);
    }

    public static String[] properties(String name) {
        return __RVM__.properties(name);
    }

    public static String value(String name) {
        return __RVM__.value(name);
    }

    public static String setValue(String name, String value) {
        return __RVM__.setValue(name, value.getBytes());
    }

    public static String setValue(String name, byte[] value) {
        return __RVM__.setValue(name, value);
    }

    public static String datatype(String name) {
        return __RVM__.datatype(name);
    }
    //-------------------------------------------------------------
    //END RVM CODE
    //-------------------------------------------------------------
}
