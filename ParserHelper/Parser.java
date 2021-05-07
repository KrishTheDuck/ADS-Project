package EOY_ADS_PROJECT.Compiler.ParserHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*TODO FIXME just when i thought things were going my way i realized that this parser only works for numbers. kill me.
 * booleans contain unary operators that don't fit the same execution style. so what you have to do is make a lower Parser which extends this one,
 * and move the eval_token and execute methods down to that one
 * also make a UnaryParser class that also supports unary operations so bitwise is possible. That, or push bitwise into Parser and try to fit boolean in somehow
 * NOTEME above problem fixed as of march 17th
 */
public abstract class Parser {

    public abstract String b_execute(String a, String b, String binary_operator);

    public abstract String u_execute(String a, String unary_operator);

    public abstract String[] binary_operators();

    public abstract String[] unary_operators();

    public abstract String binary_regex();

    public abstract String assert_format(String expression);

    public static String raw_binary_operators(Parser p) {
        StringBuilder raw = new StringBuilder();
        for (String op : p.binary_operators()) {
            raw.append(op);
        }
        return raw.toString();
    }

    public static String raw_unary_operators(Parser p) {
        StringBuilder raw = new StringBuilder();
        for (String op : p.unary_operators()) {
            raw.append(op);
        }
        return raw.toString();
    }

    public static String EVALUATE(String expression, Parser p) {
        expression = p.assert_format(expression);
        System.out.println("Correct expression: " + expression);
        String line;
        while (!(line = earliest_expression(expression)).equals(expression)) {
            expression = expression.replace(line, eval_token(line, p));
        }
        return eval_token(expression, p);
    }

    private static String eval_token(String token, Parser p) {
        token = token.replaceAll("[() ]", "").strip();
        System.out.println("Parser: Evaluating " + token);

        System.out.println("Simplifying unary operators");

        if (p.unary_operators().length > 0) {
            String regex = "[" + raw_unary_operators(p) + "](.*?)+[" + raw_binary_operators(p) + "]";
            Matcher m = Pattern.compile(regex).matcher(token);

            while (m.find()) {
                String line = m.group();
                String ans = p.u_execute(line.substring(1), String.valueOf(line.charAt(0)));
                token = token.replace(line, ans);
            }
        }

        ArrayList<String> terms = new ArrayList<>(Arrays.asList(token.split(p.binary_regex())));
        System.out.println("Parser: printing terms: " + terms);

        terms = new ArrayList<>(Arrays.asList(token.split(p.binary_regex())));

        for (String operator : p.binary_operators()) {
            String c = String.valueOf(operator);
            while (terms.contains(c)) {
                System.out.println("Parser: printing simplification: " + terms);
                int index = terms.indexOf(c);
                String e = terms.get(index - 1);
                String b = terms.get(index + 1);
                String ans = p.b_execute(e, b, c);
                terms.set(index, ans);
                terms.remove(index + 1);
                terms.remove(index - 1);
            }
        }
        System.out.println("Evaluation finished with result: " + terms.get(0) + "\n\n\n");
        return terms.get(0);
    }

    public static String earliest_expression(String statement) {
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

        return statement.substring(f_index, l_index + 1);
    }
}
