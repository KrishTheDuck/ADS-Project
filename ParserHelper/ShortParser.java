package ParserHelper;

public class ShortParser extends Parser {
    public static final String[] binary_operators = {"^", "*", "+", ">>", "<<"};
    public static final String[] unary_operators = {"~", "!"};

    public static final String binary_regex = "(?<=((<<)|(>>)|^|\\*|\\+)|(?=((<<)|(>>)|^|\\*|\\+)))";

    private static short pow(String b, String exp) {
        short base = Short.parseShort(b);
        short exponent = (short) (Short.parseShort(exp) - 1);
        short ans = base;

        while (exponent > 0) {
            ans += (base - 1) * (exponent & 1) * ans;
            exponent >>= 1;
            base *= base;
        }

        return ans;
    }

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "^" -> Short.toString(pow(a, b));
            case "*" -> Short.toString((short) (Short.parseShort(a) * Short.parseShort(b)));
            case "+" -> Short.toString((short) (Short.parseShort(a) + Short.parseShort(b)));
            case "<<" -> Short.toString((short) (Short.parseShort(a) << Short.parseShort(b)));
            case ">>" -> Short.toString((short) (Short.parseShort(a) >> Short.parseShort(b)));
            default -> throw new UnsupportedOperationException("Operators \"" + binary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return switch (unary_operator) {
            case "~" -> Short.toString((short) ~Short.parseShort(a));
            case "!" -> (Short.parseShort(a) != 0) ? "0" : "1";
            default -> throw new UnsupportedOperationException("Operators \"" + unary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String[] binary_operators() {
        return ShortParser.binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return ShortParser.unary_operators;
    }

    @Override
    public String binary_regex() {
        return ShortParser.binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        return expression.replaceAll("(?<!\\+)-", "+-");
    }
}
