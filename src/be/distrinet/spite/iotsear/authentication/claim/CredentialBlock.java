package be.distrinet.spite.iotsear.authentication.claim;

import be.distrinet.spite.iotsear.core.model.credential.CredentialType;
import com.google.common.flogger.FluentLogger;

import java.util.HashMap;
import java.util.Map;

public class CredentialBlock {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final CredentialType credentialType;
    private final Map<String, AttributeStatus> attributeStatusMap;

    /**
     * Creates a new CredentialBlock based on the provided type.
     * The AttributeStatus of each attribute is initialized to AttributeStatus.REVEAL
     *
     * @param credentialType
     */
    public CredentialBlock(final CredentialType credentialType) {
        this.credentialType = credentialType;
        this.attributeStatusMap = new HashMap<>();
        for (final String att : credentialType.getAttributes().keySet()) {
            this.attributeStatusMap.put(att, AttributeStatus.REVEAL);
        }
    }

    /**
     * Sets the claim status for the provided attribute.
     * This method has no effect if this credentialblock does not contain the provided attribute
     *
     * @param attribute
     * @param status
     * @return this CredentialBlock (for Builder pattern)
     */
    public CredentialBlock setAttributeStatus(final String attribute, final AttributeStatus status) {
        if (this.attributeStatusMap.containsKey(attribute)) {
            this.attributeStatusMap.put(attribute, status);
        } else {
            logger.atWarning().log("The CredentialType of this CredentialBlock does not have the provided attribute: %s", attribute);
        }
        return this;
    }

    public CredentialType getCredentialType() {
        return this.credentialType;
    }

    public Map<String, AttributeStatus> getAttributeStatusMap() {
        return this.attributeStatusMap;
    }

    /**
     * creates and returns a predicate block for an attribute of this credential block
     *
     * @param attribute
     * @param operator
     * @param value
     * @return the predicate block, or null if this credentialblock does not contain the provided attribute
     */
    public PredicateBlock createPredicate(final String attribute, final PredicateBlock.Operator operator, final String value) {
        if (this.attributeStatusMap.containsKey(attribute)) {
            return new PredicateBlock(this, attribute, operator, value);
        } else {
            return null;
        }
    }

    public String getProviderID() {
        return this.credentialType.getProviderID();
    }


    public enum AttributeStatus {
        REVEAL,
        OPTIONAL,
        HIDE
    }

}
