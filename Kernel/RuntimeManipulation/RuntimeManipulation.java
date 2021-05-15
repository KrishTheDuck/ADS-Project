package Kernel.RuntimeManipulation;

//todo when sealed types come out seal rm with rvm and all others
public interface RuntimeManipulation {
    void commit(String name, String... properties) throws Exception;

    void destroy(String name) throws Exception;

    String[] fetch(String name) throws Exception;

    void set(String property, String name, String value) throws Exception;

    String get(String property, String name) throws Exception;

    boolean hasName(String name);
}

