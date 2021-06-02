package Kernel.Datatypes;

public class Long extends Number {
    public long num;

    public Long(long _long) {
        this.num = _long;
    }

    public String toString() {
        return String.valueOf(num);
    }
}
