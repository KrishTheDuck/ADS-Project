package Kernel.Datatypes;

public final class NumberStringUtility {
    public static char isNum(char n) {
        return (n <= 57 && n >= 48) ? n : '\uFFFF';
    }

    public static byte isNum(byte n) {
        return (n <= 57 && n >= 48) ? n : -1;
    }
}
