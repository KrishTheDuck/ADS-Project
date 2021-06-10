package Kernel.Datatypes;

/**
 * A lighter String class that treats values as character arrays and assumes they are integers in disguise.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: May 25, 2021
 */
public final class IntString extends Data {
    private byte[] string;
    private boolean negative;

    public IntString(String string) {
        set(string);
    }

    public static int stoi(String s) {
        return stoi(s.strip().getBytes());
    }

    public static int stoi(byte[] s) {
        byte negative = (byte) (s[0] == 45 ? -1 : 1);
        int start = negative == -1 ? 1 : 0;

        int ans = 0;
        for (int i = start; i < s.length; i++) {
            ans += (s[i] - 48);
            ans *= 10;
        }
        return negative * ans / 10;
    }

    public String toString() {
        return ((negative) ? "-" : "") + new String(string);
    }

    public void set(String string) {
        string = string.strip();
        if (string.startsWith("-")) {
            this.string = string.substring(1).getBytes();
            this.negative = true;
        } else {
            this.string = string.getBytes();
            this.negative = false;
        }
    }

    public int stoi() {
        return IntString.stoi(this.string);
    }

    @Override
    public byte[] data() {
        return this.string;
    }

    @Override
    public int compareTo(Data o) {
        return ((IntString) o).stoi() - this.stoi();
    }
}
