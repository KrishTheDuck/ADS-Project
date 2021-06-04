package ParserHelper;

/**
 * A lighter String class that treats values as character arrays and assumes they are integers in disguise.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: May 25, 2021
 */
public final class IntString implements Comparable<IntString> {
    char[] string;
    boolean negative;

    public IntString(String string) {
        set(string);
    }

    public static boolean isInteger(String s) {
        for (char c : s.toCharArray()) {
            if (isNum(c) == '\uFFFF') return false;
        }
        return true;
    }

    public static char isNum(char n) {
        for (char c : "0123456789".toCharArray()) {
            if (n == c) return c;
        }
        return '\uFFFF';
    }

    public static int stoi(String s) {
        return stoi(s.strip().toCharArray());
    }

    public static int stoi(char[] s) {
        if (s[0] == 45) {
            char[] new_s = new char[s.length - 1];
            System.arraycopy(s, 1, new_s, 0, new_s.length);
            return stoi(new_s, true);
        }
        return stoi(s, false);
    }

    public static int stoi(char[] s, boolean negative) {
        int i = 0;
        for (char c : s) {
            i += (c - 48);
            i *= 10;
        }
        return ((negative) ? -1 : 1) * i / 10;
    }

    public String toString() {
        return ((negative) ? "-" : "") + new String(string);
    }

    public void set(String string) {
        string = string.strip();
        if (string.startsWith("-")) {
            this.string = string.substring(1).toCharArray();
            this.negative = true;
        } else {
            this.string = string.toCharArray();
            this.negative = false;
        }
    }

    public int stoi() {
        return IntString.stoi(this.string, this.negative);
    }

    @Override
    public int compareTo(IntString o) {
        return -stoi() + o.stoi();
    }
}
