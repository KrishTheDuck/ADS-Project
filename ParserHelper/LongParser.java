package ParserHelper;

public class LongParser extends Parser {
    public static final String[] binary_operators = {"^", "*", "+", ">>", "<<"};
    public static final String[] unary_operators = {"~", "!"};

    public static final String binary_regex = "(?<=((<<)|(>>)|^|\\*|\\+)|(?=((<<)|(>>)|^|\\*|\\+)))";

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "^" -> Long.toString(pow(a, b));
            case "*" -> Long.toString(Long.parseLong(a) * Long.parseLong(b));
            case "+" -> Long.toString(Long.parseLong(a) + Long.parseLong(b));
            case "<<" -> Long.toString(Long.parseLong(a) << Long.parseLong(b));
            case ">>" -> Long.toString(Long.parseLong(a) >> Long.parseLong(b));
            default -> throw new UnsupportedOperationException("Operator \"" + binary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return switch (unary_operator) {
            case "~" -> Long.toString(~Long.parseLong(a));
            case "!" -> (Long.parseLong(a) != 0) ? "0" : "1";
            default -> throw new UnsupportedOperationException("Operator \"" + unary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String[] binary_operators() {
        return binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return unary_operators;
    }

    @Override
    public String binary_regex() {
        return binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        return expression.replaceAll("(?<!\\+)-", "+-");
    }

    private static long pow(String b, String exp) {
        long base = Long.parseLong(b);
        int exponent = Short.parseShort(exp) - 1;
        long ans = base;

        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans;
            exponent >>= 1;
            base *= base;
        }

        return ans;
    }

}
