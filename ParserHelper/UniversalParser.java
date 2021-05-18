package ParserHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UniversalParser {
    private static final String[] bool = {"&&", "||", ">=", "<=", ">", "<", "==", "!="};
    private static final String[] num = {"*", "/", "%", "+", "-", "^"};
    private static final String[] unary = {"!", "~"};
    private static final String[] bitwise = {"&", "|", "b^", ">>", "<<", ">>>", "<<<"};

    private static final String[][] operators = {unary, num, bitwise, bool};

    public static void main(String[] args) {
        evaluate("(!0-3+!1+3+4>-3+!10>=100)&&(1+10<10)||(1+100<=10)");
    }

    private static ArrayList<ArrayList<String>> split(String expression) {
        final Matcher m = Pattern.compile("-?[0-9]+").matcher(expression);

        ArrayList<ArrayList<String>> list = new ArrayList<>(2);

        list.add(new ArrayList<>());
        list.add(new ArrayList<>());


        int start = 0;
        while (m.find()) {
            list.get(0).add(m.group());
            String line = expression.substring(start, m.start());
            if (!line.equals("") && !line.equals(" "))
                list.get(1).add(line);
            start = m.end();
        }
        if (!expression.substring(start).equals("") && !expression.substring(start).equals(" "))
            list.get(1).add(expression.substring(start));
        return list;
    }


    public static void evaluate(String expression) {
        //replace all random spaces.
        expression = expression.replaceAll("[ ]+", "");
        expression = expression.replaceAll("-", "+-");
        System.out.println("Input: " + expression);


        String line = earliest_expression(expression);
    }

    private static String earliest_expression(String statement) {
        if (!statement.contains(")") && !statement.contains("("))
            return statement;

        int l_index = 0;

        while (statement.charAt(l_index) != ')') {
            ++l_index;
        }
        int f_index = l_index;
        while (statement.charAt(f_index) != '(') {
            --f_index;
        }

        return statement.substring(f_index + 1, l_index);
    }
}
