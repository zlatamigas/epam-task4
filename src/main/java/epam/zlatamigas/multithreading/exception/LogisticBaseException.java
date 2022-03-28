package epam.zlatamigas.multithreading.exception;

public class LogisticBaseException extends Exception {
    public LogisticBaseException() {
        super();
    }

    public LogisticBaseException(String message) {
        super(message);
    }

    public LogisticBaseException(Throwable cause) {
        super(cause);
    }

    public LogisticBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
