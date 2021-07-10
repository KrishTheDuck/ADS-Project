package RuntimeManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    public static void dumpTaskLoad() {
        System.out.println(pool);
    }

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

    public static void delete(String var) {
        __RVM__.delete(var);
    }

    public static void setValue(String name, String value) {
        __RVM__.setValue(name, value.getBytes());
    }

    public static String setValue(String name, byte[] value) {
        return __RVM__.setValue(name, value);
    }

    public static String datatype(String name) {
        return __RVM__.datatype(name);
    }
    //-------------------------------------------------------------
    //END OF RVM CODE
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    //PREPROCESSOR CODE
    //-------------------------------------------------------------

    public static RandomAccessFile compile(File file) throws IOException, NoSuchFieldException, IllegalAccessException {
        return Preprocessor.compile(file);
    }
    //-------------------------------------------------------------
    //END OF PREPROCESSOR CODE
    //-------------------------------------------------------------

    //-------------------------------------------------------------
    //EXECUTOR CODE
    //-------------------------------------------------------------
    public static void execute(RandomAccessFile f) throws Exception {
        Executor.execute(f);
    }

    public static void exit() {
        pool.shutdownNow();
    }
    //-------------------------------------------------------------
    //END OF PREPROCESSOR CODE
    //-------------------------------------------------------------
}
