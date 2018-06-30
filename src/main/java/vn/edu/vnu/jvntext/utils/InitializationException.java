package vn.edu.vnu.jvntext.utils;

public class InitializationException extends Exception {
    private static final long serialVersionUID = 1L;

    public InitializationException(Exception e) {
        super(e);
    }

    public InitializationException(String message) {
        super(message);
    }
}
