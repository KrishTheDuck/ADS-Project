package FunctionLibrary;

import LanguageExceptions.FunctionNotFoundException;
import LanguageExceptions.LibraryNotFoundException;

//NOTEME for void functions return true, else return the value (boxing is fine even though its expensive worry about optimizations later)
//noteme because mfte doesnt care about what the params are supposed to look like functions should break it up themselves
public final class FLMapper {
    //TODO link the libraries to the specific functions
    public static Object mapFunctionToExecution(String library, String function_name, String args) throws LibraryNotFoundException, FunctionNotFoundException {
        switch (library.toLowerCase()) {
            case "stdio" -> {
                switch (function_name) {
                    case "printd" -> {
                        StandardIO.printDelimited(args);
                        return true;
                    }
                    case "printf" -> {
                        StandardIO.printFormat(args);
                        return true;
                    }
                    case "read" -> {
//                        StandardIO.input();
                        return true;
                    }

                    default -> throw new FunctionNotFoundException("Function in library StandardIO does not exist!");
                }
            }
            case "qmth" -> {
                switch (function_name) {
                    default -> throw new FunctionNotFoundException("Function in library StandardIO does not exist!");
                }
            }
            default -> throw new LibraryNotFoundException("Support for library \"" + library + "\" is not native.");
        }
    }
}
