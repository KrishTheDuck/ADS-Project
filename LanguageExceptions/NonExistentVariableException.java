package LanguageExceptions;

/**
 * Signals that an a variable, which does not exist or no longer exists, is trying to be operated upon.
 *
 * @author Krish Sridhar
 * Date: 3/12/2021
 */
public class NonExistentVariableException extends Exception {
    public NonExistentVariableException() {
        super();
    }

    public NonExistentVariableException(String message) {
        super(message);
    }

    public NonExistentVariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentVariableException(Throwable cause) {
        super(cause);
    }
}


