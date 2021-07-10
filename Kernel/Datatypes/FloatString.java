package Kernel.Datatypes;

import java.util.Arrays;

public final strictfp class FloatString extends Data {
    private final byte[] subZero;
    private final byte[] whole;

    public FloatString(String s) {
        //46 is '.'
        boolean negative = s.startsWith("-");
        s = negative ? s.substring(1) : s;
        byte[] arr = s.getBytes();

        int i;
        this.whole = new byte[(i = s.indexOf(46)) == -1 ? s.length() : i];
        this.subZero = new byte[(i == -1) ? 10 : s.length() - i]; // 10 places of decimal or just the rest
        System.arraycopy(arr, 0, this.whole, 0, this.whole.length);
        System.arraycopy(arr, this.whole.length, this.subZero, 0, this.subZero.length);
        System.out.println(Arrays.toString(this.whole));
        System.out.println(Arrays.toString(this.subZero));
    }

    public static void main(String[] args) {
        FloatString s = new FloatString("1.58496250072");
        System.out.println(s);
    }

    public static boolean isFloat(String s) {
        String[] num = s.split("\\.");//SPLIT IT
        switch (num.length) {
            case 1 -> {
                for (byte c : num[0].getBytes()) {
                    if (NumberStringUtility.isNum(c) == -1) return false;
                }
            }
            case 2 -> {
                for (String part : num) {
                    for (byte c : part.getBytes()) {
                        if (NumberStringUtility.isNum(c) == -1) return false;
                    }
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    public static float stof(String s) {
        return stof(s.getBytes());
    }

    public static float stof(byte[] whole, byte[] decimal) {
        float f = 0F;
        for (byte b : whole) {
            f += b;
            f *= 10;
        }

        float dec = 0.0F;
        for (byte b : decimal) {
            dec += b;
            dec /= 10;
        }
        return f + dec;
    }

    public static float stof(byte[] s) {
        byte negative = (byte) ((s[0] == 45) ? -1 : 1);
        int start = (negative == -1) ? 1 : 0;

        float f = 0F;
        while (start < s.length && s[start] != 46) {
            f *= 10;
            f += s[start++] - 48;
        }
        ++start;

        float dec = 0.0F;
        float place = 0.1F;
        while (start < s.length) {
            dec += (s[start++] - 48) * place;
            place /= 10;
        }
        return f + dec;
    }

    public float stof() {
        return FloatString.stof(this.whole, this.subZero);
    }

    @Override
    public byte[] data() {
        byte[] data = new byte[subZero.length + whole.length];
        System.arraycopy(whole, 0, data, 0, whole.length);
        System.arraycopy(subZero, 0, data, whole.length, subZero.length);
        return data;
    }

    @Override
    public int compareTo(Data o) {
        return (int) (((FloatString) o).stof() - this.stof());
    }

    @Override
    public String toString() {
        return new String(data());
    }
}
