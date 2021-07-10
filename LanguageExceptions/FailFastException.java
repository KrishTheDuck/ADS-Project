package LanguageExceptions;

public class FailFastException extends Exception {
    public FailFastException() {
        super();
    }

    public FailFastException(String message) {
        super("Fail fast activated: " + message);
    }

    public FailFastException(String message, Throwable cause) {
        super("Fail fast activated; " + message, cause);
    }

    public FailFastException(Throwable cause) {
        super(cause);
    }
}
