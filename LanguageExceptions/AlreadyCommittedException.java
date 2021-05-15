package LanguageExceptions;

/**
 * Signals that an overlap has occurred where two variables of the exact same scope and name is being added.
 *
 * @author Krish Sridhar
 * Date: 3/12/2021
 */
public class AlreadyCommittedException extends Exception {
    public AlreadyCommittedException() {
        super();
    }

    public AlreadyCommittedException(String message) {
        super(message);
    }

    public AlreadyCommittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyCommittedException(Throwable cause) {
        super(cause);
    }
}


