package FunctionLibrary;

import FunctionLibrary.Library.QuickMath;
import FunctionLibrary.Library.StandardIO;
import LanguageExceptions.FunctionNotFoundException;
import LanguageExceptions.LibraryNotFoundException;
import RuntimeManager.RuntimePool;

import java.util.Arrays;


public final class FLMapper {
    public static Object mapFunctionToExecution(String library, String function_name, String args) throws LibraryNotFoundException, FunctionNotFoundException {
        return switch (library.toLowerCase()) {
            case "stdio" -> StandardIO.map(function_name, args);
            case "qmth" -> QuickMath.map(function_name, args);
            case "sys" -> switch (function_name) {
                case "print" -> {
                    System.out.print("CONSOLE SAYS: " + args);
                    yield true;
                }
                case "println" -> {
                    System.out.println("CONSOLE SAYS: " + args);
                    yield true;
                }
                case "printf" -> {
                    args = ReplaceWithValue(args);
                    System.out.println(args);
                    yield true;
                }

                default -> throw new IllegalStateException("Unexpected value: " + function_name);
            };
            default -> throw new LibraryNotFoundException("Library is not supported: " + library.toLowerCase());
        };
    }

    public static String ReplaceWithValue(String... tokens) {
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            if (tokens[i].matches("[a-zA-Z]+[0-9]*")) {
                tokens[i] = RuntimePool.value(tokens[i]);
            }
        }
        System.out.println("Tokens: " + Arrays.toString(tokens));
        return String.join(" ", tokens);
    }
}
