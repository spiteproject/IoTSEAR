package be.distrinet.spite.iotsear.core.exceptions;

/**
 * Exception that occurs when the framework / plugins process an invalid configuration
 */
public class InvalidConfigurationException extends Exception {

    private static final long serialVersionUID = -6537943111788586265L;

    /**
     * Constructs a new exception with a message detailing that the configuration is missing
     * a required element.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param missingElement the element-identifier that is missing.
     */
    public InvalidConfigurationException(final String missingElement) {
        super("Provided configuration did not contain required element: " + missingElement);
    }

    /**
     * Constructs a new exception with a message detailing that an element of the
     * configuration contains an unexpected value.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param foundElement  the element-identifier.
     * @param foundValue    the problematic value in the configuration.
     * @param expectedValue the expected value.
     */
    public InvalidConfigurationException(final String foundElement, final String foundValue, final String expectedValue) {
        super("The element '" + foundElement + "' contains value '" + foundValue + "' but expected '" + expectedValue + "'");
    }

    /**
     * Constructs a new generic InvalidConfigurationException.
     */
    public InvalidConfigurationException() {
        super("The provided configuration is not valid");
    }
}
