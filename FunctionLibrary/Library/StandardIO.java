package FunctionLibrary.Library;

import Console.Terminal;
import FunctionLibrary.Native;
import Kernel.Data_Structures.SortedPairList;
import ParserHelper.UniversalParser;

import java.util.Arrays;

/**
 * Controls basic I/O functions.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see FunctionLibrary
 */
public final class StandardIO extends Native {
    private static final int Serial = Native.GenerateHashCode(StandardIO.class);

    public static boolean printDelimited(String message) {
        try {
            String[] tokens = message.split(",");
            //NOTEME we can add special chars to indicate different ways of delimiting the expression >:)
            tokens[0] = tokens[0].substring(1, tokens[0].length() - 1);
            String delimiter =
                    switch (tokens[0]) {
                        case "\\n" -> "\n";
                        case "\\r" -> "\r";
                        case "\\t" -> "\t";
                        default -> tokens[0];
                    };
            StringBuilder f_message = new StringBuilder();

            for (int i = 1, n = tokens.length; i < n; i++) {
                f_message.append(UniversalParser.evaluate(UniversalParser.ReplaceWithValue(tokens[i].split(" ")))).append(delimiter);
            }

            System.out.println("Delimiter: \"" + delimiter + "\"");
            System.out.println("Message: " + Arrays.toString(tokens));
            Terminal.println(false, f_message.toString());
            return true;
        } catch (Exception e) {
            Terminal.print(message);
            Terminal.print(e.toString());
            return false;
        }
    }

    public static boolean printFormat(String message) {
        try {
            if (message.charAt(0) == '\"')
                message = message.substring(1);
            if (message.charAt(message.length() - 1) == '\"')
                message = message.substring(0, message.length() - 1);

            StringBuilder rs = new StringBuilder(message);
            System.out.println("Message: " + rs);

            SortedPairList<String, String> jump_map = new SortedPairList<>();

            for (int i = 0; i < message.length(); i++) {
                if (rs.charAt(i) == '$') {
                    int index = ++i;
                    while (message.charAt(i) != '$') {
                        i++;
                    }

                    String var = rs.substring(index, i);

                    String line;
                    if (jump_map.hasKey(var)) line = jump_map.getPairFromKey(var).value();
                    else {
                        line = UniversalParser.ReplaceWithValue(var);
                        jump_map.add(var, line);
                    }

                    rs.replace(index - 1, i + 1, line);
                    i += line.length();
                }
            }

            jump_map.destroy();
            System.out.println("Message: " + rs);

            Terminal.print(rs.toString());
            return true;
        } catch (Exception e) {
            Terminal.print(message);
            Terminal.print(e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public static String readLine() {
        byte[] input = Terminal.readLine();
        System.out.println("input: " + Arrays.toString(input));
        System.out.println("input stringified: " + new String(input));
        return new String(input);
    }

    public static boolean println(String args) {
        try {
            args = args.replace("\"", "");
            Terminal.println(false, args);
            return true;
        } catch (Exception e) {
            Terminal.println(false, args);
            Terminal.println(false, e.toString());
            return false;
        }
    }

    public static boolean print(String args) {
        try {
            args = args.replace("\"", "");
//            args = args.replace_RH("\\n", "\n");
            Terminal.print(args);
            return true;
        } catch (Exception e) {
            Terminal.print(args);
            Terminal.print(e.toString());
            return false;
        }
    }

    private static String ConstructString(String arg) {

        return "";
    }

    public static Object map(String function, String args) {
        return switch (function) {
            case "printf" -> StandardIO.printFormat(args);
            case "printd" -> StandardIO.printDelimited(args);
            case "print" -> StandardIO.print(args);
            case "println" -> StandardIO.println(args);
            case "readLine" -> StandardIO.readLine();
            default -> throw new IllegalStateException("Function \"" + function + "\" does not exist in library StandardIO.");
        };
    }

    @Override
    public int getSerial() {
        return Serial;
    }

    @Override
    public int compareTo(Native o) {
        return Serial - o.getSerial();
    }
}
