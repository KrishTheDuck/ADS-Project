package Kernel.Datatypes;

import FunctionLibrary.Library.QuickMath;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegularString extends Data {
    private byte[] data;
    private int size;

    public RegularString(String s) {
        this.data = s.getBytes();
        this.size = this.data.length;
    }

    public RegularString(byte[] data) {
        this.data = data;
        this.size = this.data.length;
    }

    //delete from offset to length and replace_RH with string

    //http://www.cs.haifa.ac.il/~oren/Publications/bpsm.pdf
    public static void main(String[] args) {

    }

    public RegularString replace(int offset, int length, String replacer) {
        byte[] b = new byte[this.size + replacer.length() - offset - length + 1];
        //conserve from 0 to offset
        System.arraycopy(this.data, 0, b, 0, offset);
        //push replacer string
        System.arraycopy(replacer.getBytes(StandardCharsets.UTF_8), 0, b, offset, replacer.length());
        //add rest of the data
        System.arraycopy(this.data, offset + length, b, replacer.length() + offset, this.size - length - offset);
        this.data = b;
        return this;
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
        return malloc(this.size);
    }

    public RegularString malloc(int size) {
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
    public int compareTo(@NotNull Data data) {
        RegularString o = (RegularString) data;
        for (int i = 0; i < QuickMath.min(o.size(), this.size()); i++) {
            if (o.data[i] != this.data[i])
                return i;
        }
        return -1;
    }

    public char at(int index) {
        return (char) this.data[index];
    }

    public int length() {
        return this.data.length;
    }

    public RegularString substring(int start) {
        return substring(start, data.length - 1);
    }

    public RegularString substring(int start, int end) {
        byte[] arr = new byte[end - start + 1];
        System.arraycopy(this.data, start, arr, 0, end - start + 1);
        return new RegularString(arr);
    }

    @Override
    public byte[] data() {
        return this.data;
    }
}

