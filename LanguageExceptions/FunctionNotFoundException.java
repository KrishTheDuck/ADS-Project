package LanguageExceptions;

/**
 * Signals that called function is not only not defined in the function, but also not defined in the {@code {@linkplain FunctionLibrary}} package
 *
 * @author Krish Sridhar
 * Date: 3/16/2021
 */
public class FunctionNotFoundException extends Exception {
    public FunctionNotFoundException() {
        super();
    }

    public FunctionNotFoundException(String message) {
        super(message);
    }

    public FunctionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionNotFoundException(Throwable cause) {
        super(cause);
    }
}


