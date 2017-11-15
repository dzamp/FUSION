package exceptions;

public class EmissionException extends Exception {
    public EmissionException() {
        super();
    }

    public EmissionException(String message) {
        super(message);
    }

    public EmissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmissionException(Throwable cause) {
        super(cause);
    }

    protected EmissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
