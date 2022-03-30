package be.distrinet.spite.iotsear.core.exceptions;

public class ProviderNotFoundException extends Exception {
    private static final long serialVersionUID = -6107373941151372282L;

    public ProviderNotFoundException(final String providerID) {
        super("This system does not have a provider with ID: " + providerID);
    }
}
