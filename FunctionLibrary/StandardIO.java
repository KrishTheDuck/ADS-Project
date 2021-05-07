package EOY_ADS_PROJECT.FunctionLibrary;

import EOY_ADS_PROJECT.Compiler.Kernel.RuntimeVariableManipulation;
import EOY_ADS_PROJECT.Console.Terminal;

import java.util.Arrays;

/**
 * Controls basic I/O functions.
 *
 * @author Krish Sridhar, Kevin Wang
 * @see EOY_ADS_PROJECT.FunctionLibrary
 */
public final class StandardIO {
    public static void printReplaceVars(String[] message) {
        RuntimeVariableManipulation rvm = RuntimeVariableManipulation.RVM();
        String arg_delimiter = message[0].replaceAll("\"", "");
        System.out.println("current args: " + rvm);
        //NOTEME we can add special chars to indicate different ways of delimiting the expression >:)
        String delimiter;
        switch (arg_delimiter) {
            case "\\n" -> delimiter = "\n";
            case "\\r" -> delimiter = "\r";
            case "\\t" -> delimiter = "\t";
            default -> delimiter = arg_delimiter;
        }
        System.out.println("Delimiter: " + delimiter);
        System.out.println("Message: " + Arrays.toString(message));
        StringBuilder f_message = new StringBuilder();
        try {
            for (int i = 1, n = message.length; i < n; i++) {
                f_message.append(rvm.getValue(message[i].strip())).append(delimiter);
            }
        } catch (Exception e) {
            Terminal.print(false, message);
            Terminal.print(false, e.toString());
        }
        Terminal.println(false, f_message.toString());
    }

//    public static String input(){
//        return Terminal.readInput();
//    }

}
