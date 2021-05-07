package EOY_ADS_PROJECT.Compiler.ParserHelper;

public class StringParser extends Parser {
    public static final String[] binary_operators = {"+"};
    public static final String[] unary_operators = {};

    public static final String binary_regex = "(?<=(\\+)|(?=(\\+)))";

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch(binary_operator){
            case "+" -> a+b;
            default -> throw new UnsupportedOperationException("Operator \"" + binary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return a;
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
        return expression;
    }
}
