package exceptions;

public class AlgorithmDeclarationException extends Exception {
    public AlgorithmDeclarationException() {
        super();
    }

    public AlgorithmDeclarationException(String message) {
        super(message);
    }

    public AlgorithmDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlgorithmDeclarationException(Throwable cause) {
        super(cause);
    }

    protected AlgorithmDeclarationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}