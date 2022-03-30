package be.distrinet.spite.iotsear.core.model.credential.issuance;

import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.credential.CredentialType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IssuanceConfiguration {
    private final List<Attribute> attributes;
    private final CredentialType credentialType;

    public IssuanceConfiguration(final List<Attribute> attributeList, final CredentialType type) {
        this.attributes = attributeList;
        this.credentialType = type;
    }

    /**
     * This method will create a new IssuanceConfiguration which has all confidential information stripped from it (e.g. the private key)
     * similar to CRF
     * Always send the object obtained from this method to another party!
     *
     * @return an IssuanceConfiguration which contains only the required information which the credType issuer needs in order to issue a credType
     */
    public abstract IssuanceConfiguration getRequestForIssuer();

    /**
     * get a map containing the attribute_label - value pairs
     *
     * @return the values
     */

    public Map<String, String> getValues() {
        final Map<String, String> values = new HashMap<>();
        for (final Attribute attribute : this.attributes) {
            values.put(attribute.getType(), attribute.getValue());
        }
        return values;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    public CredentialType getCredentialType() {
        return this.credentialType;
    }


}
