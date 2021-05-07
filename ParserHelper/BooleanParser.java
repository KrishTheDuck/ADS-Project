package EOY_ADS_PROJECT.Compiler.ParserHelper;

public class BooleanParser extends Parser {
    public static final String[] binary_operators = {"&&", "||"};
    public static final String[] unary_operators = {"!"};

    public static final String binary_regex = "(?<=((\\|\\|)|&&)|(?=((\\|\\|)|&&)))";

    @Override
    public String b_execute(String a, String b, String binary_operator) {
        return switch (binary_operator) {
            case "&&" -> String.valueOf(supported(a) && supported(b));
            case "||" -> String.valueOf(supported(a) || supported(b));
            default -> throw new UnsupportedOperationException("Unexpected value: " + binary_operator);
        };
    }

    @Override
    public String u_execute(String a, String unary_operator) {
        return switch (unary_operator) {
            case "!" -> (supported(a)) ? "1" : "0";
            default -> throw new UnsupportedOperationException("Operator \"" + unary_operator + "\" is unsupported.");
        };
    }

    @Override
    public String[] binary_operators() {
        return BooleanParser.binary_operators;
    }

    @Override
    public String[] unary_operators() {
        return BooleanParser.unary_operators;
    }

    @Override
    public String binary_regex() {
        return BooleanParser.binary_regex;
    }

    @Override
    public String assert_format(String expression) {
        expression = expression.replace("true", "1").replace("TRUE", "1");
        expression = expression.replace("t", "1").replace("T", "1");
        expression = expression.replace("yes", "1").replace("YES", "1");
        expression = expression.replace("y", "1").replace("Y", "1");

        expression = expression.replace("false", "0").replace("FALSE", "0");
        expression = expression.replace("f", "0").replace("F", "0");
        expression = expression.replace("no", "0").replace("NO", "0");
        expression = expression.replace("n", "0").replace("N", "0");
        return expression;
    }

    private boolean supported(String a) {
        if (a.equals("true") || a.equals("1") || a.equals("yes") || a.equals("T") || a.equals("Y")) {
            return true;
        } else if (a.equals("false") || a.equals("0") || a.equals("no") || a.equals("F") || a.equals("N")) {
            return false;
        }
        throw new IllegalArgumentException("Argument \"" + a + "\" is an unsupported boolean value");
    }
}
