package FunctionLibrary;

import Console.Terminal;
import Kernel.RuntimeManipulation.RuntimePool;
import ParserHelper.UniversalParser;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controls basic I/O functions.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see FunctionLibrary
 */
public final class StandardIO {
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
                f_message.append(UniversalParser.evaluate(ReplaceWithValue(tokens[i].split(" ")))).append(delimiter);
            }
            System.out.println("Delimiter: \"" + delimiter + "\"");
            System.out.println("Message: " + Arrays.toString(tokens));
            Terminal.println(false, f_message.toString());
            return true;
        } catch (Exception e) {
            Terminal.print(false, message);
            Terminal.print(false, e.toString());
            return false;
        }
    }

    public static String ReplaceWithValue(String... tokens) {
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            if (tokens[i].matches("[a-zA-Z]+[0-9]*")) {
                tokens[i] = RuntimePool.value(tokens[i]);
            }
        }
        return String.join(" ", tokens);
    }

    public static boolean printFormat(String message) {
        try {
            String regex = "\\$\\([a-zA-Z]+[0-9]*\\)";

            int len = 0;
            for (char c : message.toCharArray()) {
                len = (c == ',') ? ++len : len;
            }

            Map<String, String> toReplace = new TreeMap<>();
            Matcher m = Pattern.compile(regex).matcher(message);
            while (m.find()) {
                String line = m.group();
                toReplace.put(line, ReplaceWithValue(line.substring(2, line.length() - 1)));
            }
            for (Map.Entry<String, String> entry : toReplace.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }

            if (message.charAt(0) == '\"')
                message = message.substring(1);
            if (message.charAt(message.length() - 1) == '\"')
                message = message.substring(0, message.length() - 1);

            Terminal.print(false, message);
            return true;
        } catch (Exception e) {
            Terminal.print(false, message);
            Terminal.print(false, e.toString());
            return false;
        }
    }

    public static byte[] readLine() {
        return Terminal.readLine();
    }

    public static boolean print(String args) {
        try {
            args = args.replace("\"", "");
            Terminal.println(false, args);
            return true;
        } catch (Exception e) {
            Terminal.print(false, args);
            Terminal.print(false, e.toString());
            return false;
        }
    }
}
