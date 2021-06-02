package ParserHelper;

import FunctionLibrary.QuickMath;
import Kernel.Data_Structures.OrderedPairList;
import Kernel.Data_Structures.Pair;
import Kernel.Data_Structures.PairList;
import Kernel.Data_Structures.SortedPairList;

import java.util.ArrayList;
import java.util.Arrays;
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
            System.out.println(line + " = " + ans);
            expression = expression.replace("(" + line + ")", ans);
            System.out.println("Expression: " + expression);
        }
        return evaluate_token(expression);
    }

    //evaluates a singular expression token
    private static String evaluate_token(String line) {
        System.out.println("Input: \"" + line + "\"");

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
        System.out.println("Numbers: " + numbers);
        System.out.println("Operators: " + operators);

        int[] map = Operators.order(operators);
        System.out.println("Order operators w index: " + Arrays.toString(map));

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
                case ">=" -> num1 >= num2 ? 1 : 0;
                case "<=" -> num1 <= num2 ? 1 : 0;
                case ">" -> num1 > num2 ? 1 : 0;
                case "<" -> num1 < num2 ? 1 : 0;
                case "==" -> num1.equals(num2) ? 1 : 0;
                case "!=" -> !num1.equals(num2) ? 1 : 0;
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };

            System.out.println("num1: " + num1);
            System.out.println("num2: " + num2);
            System.out.println("ans: " + ans);
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

            System.out.println("i: " + i + " -> " + map[i]);
            System.out.println("Operators: " + operators);
            System.out.println("Numbers: " + numbers);
            System.out.println("Map: " + Arrays.toString(map));
            System.out.println();
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


/**
 * A lighter String class that treats values as character arrays and assumes they are integers in disguise.
 *
 * @author Krish Sridhar
 * @since 1.0
 * Date: May 25, 2021
 */
final class IntString implements Comparable<IntString> {
    char[] string;
    boolean negative;

    public IntString(String string) {
        set(string);
    }

    public static boolean isInteger(String s) {
        for (char c : s.toCharArray()) {
            if (isNum(c) == '\uFFFF') return false;
        }
        return true;
    }

    public static char isNum(char n) {
        for (char c : "0123456789".toCharArray()) {
            if (n == c) return c;
        }
        return '\uFFFF';
    }

    public static int stoi(String s) {
        return stoi(s.strip().toCharArray());
    }

    public static int stoi(char[] s) {
        if (s[0] == 45) {
            char[] new_s = new char[s.length - 1];
            System.arraycopy(s, 1, new_s, 0, new_s.length);
            return stoi(new_s, true);
        }
        return stoi(s, false);
    }

    public static int stoi(char[] s, boolean negative) {
        int i = 0;
        for (char c : s) {
            i += (c - 48);
            i *= 10;
        }
        return ((negative) ? -1 : 1) * i / 10;
    }

    public String toString() {
        return ((negative) ? "-" : "") + new String(string);
    }

    public void set(String string) {
        string = string.strip();
        if (string.startsWith("-")) {
            this.string = string.substring(1).toCharArray();
            this.negative = true;
        } else {
            this.string = string.toCharArray();
            this.negative = false;
        }
    }

    public int stoi() {
        return IntString.stoi(this.string, this.negative);
    }

    @Override
    public int compareTo(IntString o) {
        return -stoi() + o.stoi();
    }
}

/**
 * A class that asserts the operator precedence of the language.
 *
 * @author Krish Sridhar
 * @see PairList
 * @see SortedPairList
 * @see OrderedPairList
 * @see Pair
 * @since 1.0
 */
final class Operators {
    public static final PairList<String, Integer> operators = new OrderedPairList<>(21) {
        {
            add("!", 0);
            add("~", 0);
            //cast goes here
            add("%", 1);
            add("*", 1);
            add("**", 1);
            add("/", 1);
            add("+", 2);
            add("-", 2);
            add("<<", 3);
            add(">>", 3);
            add(">>>", 3);
            add("<", 4);
            add("<=", 5);
            add(">", 5);
            add(">=", 5);
            add("==", 6);
            add("!=", 6);
            add("&", 7);
            add("^", 8);
            add("|", 9);
            add("&&", 10);
            add("||", 11);
        }
    }; //operator precedence

    /**
     * Accepts a list of operators and then returns the order they should be evaluated with in order of highest to lowest precedence.
     *
     * @param operators List of operators.
     * @return An integer array containing indexes of which operators to evaluate first, in order of highest to lowest precedence.
     * @see PairList
     * @see OrderedPairList
     */
    public static int[] order(List<String> operators) {
        PairList<String, Integer> map = new OrderedPairList<>(operators.size());
        for (int i = 0, MAX = operators.size(); i < MAX; i++) {
            map.add(operators.get(i), i);
        }
        return order(map);
    }

    /**
     * Accepts a <code>PairList</code> of operators.
     *
     * @param map A List of operator-index pairs.
     * @return An integer array containing indexes of which operators to evaluate first, in order of highest to lowest precedence.
     * @see Operators#order(List)
     * @see SortedPairList
     * @see Pair
     */
    public static int[] order(final PairList<String, Integer> map) {

        System.out.println("given map: " + map);

        PairList<Integer, Integer> precedence = new SortedPairList<>(map.size());

        //swap operators for ints
        for (Pair<String, Integer> entry : map) {
            precedence.add(PairList.getPairFromKey(entry.key(), operators).value(), entry.value());
            System.out.println(precedence);
        }

        System.out.println("given precedence: " + precedence);

        //now precedence is the sorted order
        int[] order = new int[precedence.size()];
        int i = 0;
        for (Pair<Integer, Integer> entry : precedence) {
            order[i++] = entry.value();
        }

        return order;
    }
}
