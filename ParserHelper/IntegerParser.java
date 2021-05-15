package ParserHelper;

//TODO FIXME consider order of operations for binary and unary operators
public class IntegerParser extends Parser {
    public static final String[] binary_operators = {"^", "*", "+", ">>", "<<"};
    public static final String[] unary_operators = {"~", "!"};

    public static final String binary_regex = "(?<=((<<)|(>>)|^|\\*|\\+)|(?=((<<)|(>>)|^|\\*|\\+)))";

    public static boolean isInteger(String s) {
        for (char c : s.toCharArray()) {
            if ((int) c >= 65 && (int) c <= 122) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "^" -> Integer.toString(pow(a, b));
            case "*" -> Integer.toString(Integer.parseInt(a) * Integer.parseInt(b));
            case "+" -> Integer.toString(Integer.parseInt(a) + Integer.parseInt(b));
            case "<<" -> Integer.toString(Integer.parseInt(a) << Integer.parseInt(b));
            case ">>" -> Integer.toString(Integer.parseInt(a) >> Integer.parseInt(b));
            default -> throw new UnsupportedOperationException("Operator \"" + binary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return switch (unary_operator) {
            case "~" -> Integer.toString(~Integer.parseInt(a));
            case "!" -> (Integer.parseInt(a) != 0) ? "0" : "1";
            default -> throw new UnsupportedOperationException("Operator \"" + unary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String[] binary_operators() {
        return IntegerParser.binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return unary_operators;
    }

    @Override
    public String binary_regex() {
        return IntegerParser.binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        return expression.replaceAll("(?<!\\+)-", "+-");
    }
    //TODO use quikkmath
    private static int pow(String b, String exp) {
        int base = Integer.parseInt(b);
        int exponent = Short.parseShort(exp) - 1;
        int ans = base;

        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans;
            exponent >>= 1;
            base *= base;
        }

        return ans;
    }
}
