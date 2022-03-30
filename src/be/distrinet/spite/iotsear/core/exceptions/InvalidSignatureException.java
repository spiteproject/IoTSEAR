package be.distrinet.spite.iotsear.core.exceptions;

public class InvalidSignatureException extends VerificationException {
    private static final long serialVersionUID = -4818391448406819330L;

    public InvalidSignatureException(Exception e) {
        super(e);
    }
}