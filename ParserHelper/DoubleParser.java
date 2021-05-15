package ParserHelper;

public class DoubleParser extends Parser {
    public static final String[] binary_operators = {"^", "*", "+"};
    public static final String[] unary_operators = {};

    public static final String binary_regex = "(?<=(\\^|\\*|\\+)|(?=(\\^|\\*|\\+)))";

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "^" -> Double.toString(pow(a, b));
            case "*" -> Double.toString(Double.parseDouble(a) * Double.parseDouble(b));
            case "+" -> Double.toString(Double.parseDouble(a) + Double.parseDouble(b));
            default -> throw new UnsupportedOperationException("Operator \"" + binary_operator + "\" is unsupported.");
        };
    }

    private static double pow(String a, String exp) {
        double base = Double.parseDouble(a);
        short exponent = (short) (Short.parseShort(exp) - 1);
        double ans = base;

        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans;
            exponent >>= 1;
            base *= base;
        }

        return ans;
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return a;
    }

    @Override
    public String[] binary_operators() {
        return DoubleParser.binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return DoubleParser.unary_operators;
    }

    @Override
    public String binary_regex() {
        return DoubleParser.binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        return expression.replaceAll("(?<!\\+)-", "+-");
    }
}
