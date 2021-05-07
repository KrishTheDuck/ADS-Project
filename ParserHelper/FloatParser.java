package EOY_ADS_PROJECT.Compiler.ParserHelper;

public class FloatParser extends Parser {
    public static final String[] binary_operators = {"^", "*", "+"};
    public static final String[] unary_operators = {};

    public static final String binary_regex = "(?<=(\\^|\\*|\\+)|(?=(\\^|\\*|\\+)))";

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "^" -> Float.toString(pow(a, b));
            case "*" -> Float.toString(Float.parseFloat(a) * Float.parseFloat(b));
            case "+" -> Float.toString(Float.parseFloat(a) + Float.parseFloat(b));
            default -> throw new UnsupportedOperationException("Operator \"" + binary_operator + "\" is unsupported.");
        };
    }

    private static float pow(String a, String exp) {
        float base = Float.parseFloat(a);
        int exponent = Short.parseShort(exp) - 1;
        float ans = base;

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
        return FloatParser.binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return FloatParser.unary_operators;
    }

    @Override
    public String binary_regex() {
        return FloatParser.binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        return expression.replaceAll("(?<!\\+)-", "+-");
    }
}
