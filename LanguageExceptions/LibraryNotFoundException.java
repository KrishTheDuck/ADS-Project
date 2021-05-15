package LanguageExceptions;

/**
 * Signals that the called library has not been added to {@code {@linkplain FunctionLibrary}}
 *
 * @author Krish Sridhar
 * Date: 3/16/2021
 */
public class LibraryNotFoundException extends Exception {
    public LibraryNotFoundException() {
        super();
    }

    public LibraryNotFoundException(String message) {
        super(message);
    }

    public LibraryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryNotFoundException(Throwable cause) {
        super(cause);
    }
}


