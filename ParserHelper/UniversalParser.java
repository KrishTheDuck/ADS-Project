package ParserHelper;

import FunctionLibrary.QuickMath;
import Kernel.Datatypes.IntString;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A utility class that parses an expression into a single value.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: June 1, 2021
 */
public final class UniversalParser {
    /**
     * Accepts an expression and procedurally solves the expression until a singular value is obtained.
     *
     * @param expression String expression
     * @return String value
     */
    public static String evaluate(String expression) {
        String line;
        while (!(line = earliest_expression(expression)).equals(expression)) {
            String ans = evaluate_token(line);
            //System.out.println(line + " = " + ans);
            expression = expression.replace("(" + line + ")", ans);
            //System.out.println("Expression: " + expression);
        }
        return evaluate_token(expression);
    }

    //evaluates a singular expression token
    private static String evaluate_token(String line) {
        //System.out.println("Input: \"" + line + "\"");

        StringTokenizer st = new StringTokenizer(line, " ");
        List<Integer> numbers = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        while (st.hasMoreTokens()) {
            String token = st.nextToken().strip();
            String num = token.replaceAll("[^0-9]+", "");
            String operator = token.replaceAll("[0-9]+", "");
            if (num.equals("") || num.equals(" ")) {
                operators.add(operator);
            } else if (!" ".equals(operator) && !"".equals(operator)) {
                int i;
                switch (operator) {
                    case "!", "~" -> {
                        i = IntString.stoi(num);
                        i = (operator.equals("!")) ? (i == 0) ? 1 : 0 : ~i;
                    }
                    case "-" -> i = IntString.stoi(token);
                    default -> throw new IllegalStateException("Unexpected value: " + operator);
                }
                numbers.add(i);
            } else {
                numbers.add(IntString.stoi(num));
            }
        }
        //System.out.println("Numbers: " + numbers);
        //System.out.println("Operators: " + operators);

        int[] map = Operators.order(operators);
        //System.out.println("Order operators w index: " + Arrays.toString(map));

        //order of operators
        //evaluate the numbers in order
        for (int i = 0, mapLength = map.length; i < mapLength; i++) { //i keeps track of haw many operators have been calculated
            Integer num1 = numbers.get(map[i]);
            Integer num2 = numbers.get(map[i] + 1);
            String op = operators.get(map[i]);
            int ans = switch (op) {
                case "*" -> num1 * num2;
                case "/" -> num1 / num2;
                case "%" -> num1 % num2;
                case "+" -> num1 + num2;
                case "-" -> num1 - num2;
                case "**" -> QuickMath.POW(num1, num2);
                case "&" -> num1 & num2;
                case "|" -> num1 | num2;
                case "^" -> num1 ^ num2;
                case ">>" -> num1 >> num2;
                case "<<" -> num1 << num2;
                case ">>>" -> num1 >>> num2;
                case "&&" -> ((num1 == 1) && (num2 == 1)) ? 1 : 0;
                case "||" -> (num1 == 1) || (num2 == 1) ? 1 : 0;
                case ">=" -> num1 >= num2 ? num1 : num2;
                case "<=" -> num1 <= num2 ? num2 : num1;
                case ">" -> num1 > num2 ? num1 : num2;
                case "<" -> num1 < num2 ? num2 : num1;
                case "==" -> num1.equals(num2) ? 1 : 0;
                case "!=" -> !num1.equals(num2) ? 1 : 0;
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };

            //System.out.println("num1: " + num1);
            //System.out.println("num2: " + num2);
            //System.out.println("ans: " + ans);
            //we have the answer but we need to remove the used up numbers and operator
            //but since the index order array will still point to a value after its removed we need to subtract 1 from
            //all the values above the index

            //so first we replace
            numbers.set(map[i], ans);
            //get rid of the operator and next number
            numbers.remove(map[i] + 1);
            operators.remove(map[i]);

            //next we subtract
            for (int j = i, length = map.length; j < length; j++) {
                map[j] = (map[j] > map[i]) ? map[j] - 1 : map[j];
            }

            //System.out.println("i: " + i + " -> " + map[i]);
            //System.out.println("Operators: " + operators);
            //System.out.println("Numbers: " + numbers);
            //System.out.println("Map: " + Arrays.toString(map));
            //System.out.println();
        }
        return String.valueOf(numbers.remove(0));
    }

    //gets the most-nested expression in the entire expression
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


