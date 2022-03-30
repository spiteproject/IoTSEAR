package be.distrinet.spite.iotsear.core.exceptions;

public class VerificationException extends Exception {
    private static final long serialVersionUID = 3478785263154820794L;

    public VerificationException(final String message) {
        super(message);
    }
    public VerificationException(final Exception e) {
        super(e);
    }
}
