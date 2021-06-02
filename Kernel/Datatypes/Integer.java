package Kernel.Datatypes;

public class Integer extends Number {
    public int num;

    public Integer(int _int) {
        this.num = _int;
    }

    public String toString() {
        return String.valueOf(num);
    }
}
