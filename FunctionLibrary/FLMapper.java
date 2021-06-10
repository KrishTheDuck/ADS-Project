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
                    case "printf" -> {
                        return StandardIO.printFormat(args);
                    }
                    case "printd" -> {
                        return StandardIO.printDelimited(args);
                    }
                    case "print" -> {
                        return StandardIO.print(args);
                    }
                    case "println" -> {
                        return StandardIO.println(args);
                    }
                    case "readLine" -> {
                        return StandardIO.readLine();
                    }
                    default -> throw new IllegalStateException("Function does not exist in library \"" + library + "\": " + function_name);
                }
            }
            case "qmth" -> {
                throw new LibraryNotFoundException("QMTH library not implemented");
            }
            default -> throw new LibraryNotFoundException("Library is not supported: " + library.toLowerCase());
        }
    }
}
