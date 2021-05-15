package LanguageExceptions;

/**
 * Signals that an overlap has occurred when committing two structs of the same name and parameters.
 *
 * @author Krish Sridhar
 * Date: 3/12/2021
 */
public class StructOverlapException extends Exception {
    public StructOverlapException() {
        super();
    }

    public StructOverlapException(String message) {
        super(message);
    }

    public StructOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

    public StructOverlapException(Throwable cause) {
        super(cause);
    }
}


