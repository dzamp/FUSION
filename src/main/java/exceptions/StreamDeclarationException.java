package exceptions;

public class StreamDeclarationException extends Exception {
    public StreamDeclarationException() {
        super();
    }

    public StreamDeclarationException(String message) {
        super(message);
    }

    public StreamDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamDeclarationException(Throwable cause) {
        super(cause);
    }

    protected StreamDeclarationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
