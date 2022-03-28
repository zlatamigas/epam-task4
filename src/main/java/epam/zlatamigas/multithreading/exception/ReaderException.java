package epam.zlatamigas.multithreading.exception;

public class ReaderException extends Exception {
    public ReaderException() {
        super();
    }

    public ReaderException(String message) {
        super(message);
    }

    public ReaderException(Throwable cause) {
        super(cause);
    }

    public ReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
