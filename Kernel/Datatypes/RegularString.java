package Kernel.Datatypes;

import FunctionLibrary.QuickMath;

import java.util.Arrays;

public class RegularString extends Data {
    private byte[] data;
    private int size;

    public RegularString(String s) {
        this.data = s.getBytes();
        this.size = this.data.length;
    }

    public RegularString add(String s) {
        return add(s.getBytes());
    }

    public RegularString add(byte[] s) {
        if (s.length + size > this.data.length) {
            int pref_growth = size + s.length + (size + s.length) >> 1;
            this.data = Arrays.copyOf(this.data, pref_growth);
            System.arraycopy(s, 0, this.data, size, s.length);
            size = pref_growth;
        } else if (data.length - size >= 0) System.arraycopy(s, size, data, size, data.length - size);
        return this;
    }

    public RegularString fit() {
        this.data = Arrays.copyOf(this.data, size);
        return this;
    }

    public RegularString del(int offset, int count) {
        if (size - (offset + count) >= 0 && offset >= 0) {
            byte[] arr = new byte[size - count];
            System.arraycopy(this.data, 0, arr, 0, offset);
            System.arraycopy(this.data, offset + count, arr, offset + count - count, size - (offset + count));
            size -= count;
            this.data = arr;
        }
        return this;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        return new String(this.data);
    }

    /**
     * @return int index of difference between to Regular Strings
     */
    @Override
    public int compareTo(Data data) {
        RegularString o = (RegularString) data;
        for (int i = 0; i < QuickMath.min(o.size(), this.size()); i++) {
            if (o.data[i] != this.data[i])
                return i;
        }
        return -1;
    }

    @Override
    public byte[] data() {
        return this.data;
    }
}

