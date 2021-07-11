package ParserHelper;

import FunctionLibrary.FLMapper;
import FunctionLibrary.Library.QuickMath;
import Kernel.Datatypes.FloatString;
import Kernel.Datatypes.IntString;
import LanguageExceptions.FunctionNotFoundException;
import LanguageExceptions.LibraryNotFoundException;
import RuntimeManager.PreprocessorFlags;
import RuntimeManager.RuntimePool;
import RuntimeManager.SharedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
            ////System.out.println(line + " = " + ans);
            expression = expression.replace("(" + line + ")", ans);
            ////System.out.println("Expression: " + expression);
        }
        return evaluate_token(expression);
    }

    public static void main(String[] args) {
        //System.out.println("ans: " + s);
    }

    public static String function_evaluate(String expression) throws LibraryNotFoundException, FunctionNotFoundException {
        String line;
        while (!(line = innermost_function_call(expression)).equals(expression)) {
            //System.out.println("line: " + line);
            String[] broken = break_call(line);
            expression = expression.replace(line, FLMapper.mapFunctionToExecution(broken[0], broken[1], broken[2]).toString());
            //System.out.println("Expression: " + expression);
        }
        String[] broken = break_call(line);
        return FLMapper.mapFunctionToExecution(broken[0], broken[1], broken[2]).toString(); //noteme floatstring is incorrectly converting string into float
    }

    public static String[] break_call(String expression) {
        int i1 = expression.indexOf(PreprocessorFlags.call_delim);
        int i2 = expression.indexOf("(");
        String library = expression.substring(0, i1);
        String function = expression.substring(i1 + PreprocessorFlags.call_delim.length(), i2);
        String[] args = expression.substring(i2 + 1, expression.lastIndexOf(")")).split(PreprocessorFlags.pdelim);

        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            //System.out.println("\tArg: " + args[i]);
            args[i] = evaluate(args[i]);
        }
        return new String[]{library, function, String.join(" ", args)};
    }

    private static String innermost_function_call(String expression) {
        int index = expression.lastIndexOf(PreprocessorFlags.call_delim);
        if (index == -1) return expression;

        //library is between the latest occurrence of ' ', ',' , or '('
        int begin = 0;
        for (int i = index - 1; i >= 0; i--) { //todo implement earliest expression for functions
            char c = expression.charAt(i);
            if (c == ' ' || c == ',' || c == '(') {
                begin = i + 1;
                break;
            }
        }
        int end = 0;
        for (int i = index; i < expression.length(); i++) {
            if (expression.charAt(i) == ')') {
                end = i + 1;
                break;
            }
        }
        return expression.substring(begin, end);
    }

    public static String scopeIt(Stack<String> scopes) {
        StringBuilder sb = new StringBuilder();
        for (String s : scopes) {
            sb.append(s).append('.');
        }
        return sb.toString();
    }

    public static String ReplaceWithValue(String... tokens) {
        //System.out.println("Tokens: " + Arrays.toString(tokens));
        String scope = scopeIt(SharedData.current_scope);
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            if (tokens[i].matches("[a-zA-Z]+[0-9]*")) {
                System.out.println("Finding valueof: " + (scope + tokens[i]));
                tokens[i] = RuntimePool.value(scope + tokens[i]);
            }
        }
        //System.out.println("Tokens: " + Arrays.toString(tokens));
        return String.join(" ", tokens);
    }


    //evaluates a singular expression token
    private static String evaluate_token(String line) {
        ////System.out.println("Input: \"" + line + "\"");
        if (line.startsWith("\"") && line.endsWith("\"")) return line;
        String[] st = line.split(" ");
        if (st.length == 1) return line;

        ReplaceWithValue(st);
        //System.out.println(ReplaceWithValue(st));

        List<Float> numbers = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        //System.out.println("Token: " + Arrays.toString(st));
        for (String string : st) {
            string = string.strip();
            if (FloatString.isFloat(string) || IntString.isInteger(string)) {
                numbers.add(FloatString.stof(string));
            } else {
                String operator = string.replaceAll("[0-9]+?[.]", "");
                if (operator.length() == string.length()) {
                    operators.add(operator);
                    continue;
                }
                String num = string.replace(operator, "");
                float i;
                switch (operator) {
                    case "!", "~" -> {
                        i = FloatString.stof(num);
                        i = (operator.equals("!")) ? (i == 0) ? 1 : 0 : ~(int) i;
                    }
                    case "-" -> i = FloatString.stof(string);
                    case "." -> {
                        continue;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + operator);
                }
                numbers.add(i);
            }
        }
        //System.out.println("Numbers: " + numbers);
        //System.out.println("Operators: " + operators);

        int[] map = Operators.order(operators);
        ////System.out.println("Order operators w index: " + Arrays.toString(map));

        //order of operators
        //evaluate the numbers in order
        boolean isFalse = false;
        for (int i = 0, mapLength = map.length; i < mapLength; i++) { //i keeps track of haw many operators have been calculated
            float num1 = numbers.get(map[i]);
            float num2 = numbers.get(map[i] + 1);
            String op = operators.get(map[i]);
            float ans = switch (op) {
                case "*" -> num1 * num2;
                case "/" -> num1 / num2;
                case "%" -> num1 % num2;
                case "+" -> num1 + num2;
                case "-" -> num1 - num2;
                case "**" -> (float) QuickMath.POW(num1, (int) num2);
                case "&" -> (int) num1 & (int) num2;
                case "|" -> (int) num1 | (int) num2;
                case "^" -> (int) num1 ^ (int) num2;
                case ">>" -> (int) num1 >> (int) num2;
                case "<<" -> (int) num1 << (int) num2;
                case ">>>" -> (int) num1 >>> (int) num2;
                case "&&" -> {
                    if (!isFalse && num1 == 1 && num2 == 1) { //if already found to be false don't bother
                        yield 1;
                    }
                    isFalse = true;
                    yield 0;
                }
                case "||" -> {
                    if (num1 == 1 || num2 == 1 || !isFalse) { //just in case
                        isFalse = false;
                        yield 1;
                    }
                    isFalse = true;
                    yield 0;
                }
                case ">=" -> {
                    if (!isFalse && num1 >= num2) {
                        yield num1;
                    }
                    isFalse = true;
                    yield 0;
                }
                case "<=" -> {
                    if (!isFalse && num1 <= num2) {
                        yield num2;
                    }
                    isFalse = true;
                    yield 0;
                }
                case ">" -> {
                    if (!isFalse && num1 > num2) {
                        yield num1;
                    }
                    isFalse = true;
                    yield 0;
                }
                case "<" -> {
                    if (!isFalse && num1 < num2) {
                        yield num2;
                    }
                    isFalse = true;
                    yield 0;
                }
                case "==" -> {
                    if (!isFalse && num1 == num2) {
                        yield num1;
                    }
                    isFalse = true;
                    yield 0;
                }
                case "!=" -> {
                    if (!isFalse && num1 != num2) {
                        yield num1;
                    }
                    isFalse = true;
                    yield 0;
                }
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };

            //System.out.println("num1: " + num1);
            //System.out.println("num2: " + num2);
            //System.out.println("ans: " + ans);
            //System.out.println("Is false: " + isFalse);
            //we have the answer but we need to remove the used up numbers and operator
            //but since the index order array will still point to a value after its removed we need to subtract 1 from
            //all the values above the index

            //so first we replace_RH
            numbers.set(map[i], ans);
            //get rid of the operator and next number
            numbers.remove(map[i] + 1);
            operators.remove(map[i]);

            //next we subtract
            for (int j = i, length = map.length; j < length; j++) {
                map[j] = (map[j] > map[i]) ? map[j] - 1 : map[j];
            }

            ////System.out.println("i: " + i + " -> " + map[i]);
            //System.out.println("Operators: " + operators);
            //System.out.println("Numbers: " + numbers);
            ////System.out.println("Map: " + Arrays.toString(map));
            //System.out.println();
        }
        float i = numbers.remove(0);
        //System.out.println("Result: " + str);
        return String.valueOf((i != 0.0) ? ((isFalse) ? 0 : i) : 0);
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


