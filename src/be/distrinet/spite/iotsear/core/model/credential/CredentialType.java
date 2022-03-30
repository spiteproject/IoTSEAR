package be.distrinet.spite.iotsear.core.model.credential;


import be.distrinet.spite.iotsear.core.model.credential.issuance.Issuer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CredentialType {


    private final Map<String, String> attributes;
    private Issuer issuer;

    public CredentialType(final Map<String, String> attributes, final Issuer issuer) {
        this.attributes = attributes;
        this.issuer = issuer;
    }


    public boolean matches(final Credential credential) {
        return this.equals(credential.getCredentialType());
    }

    public boolean hasAttributes(final List<String> attributes) {
        return this.attributes.keySet().containsAll(attributes) && this.attributes.size() == attributes.size();
    }

    public boolean hasAttribute(final String attribute) {
        return this.attributes.containsKey(attribute);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public void setIssuer(final Issuer issuer) {
        this.issuer = issuer;
    }

    @Override
    public int hashCode() {

        return Objects.hash(CredentialType.class, this.attributes, this.issuer);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.hashCode() != obj.hashCode() && !(obj instanceof CredentialType)) {
            return false;
        }
        final CredentialType ct2 = (CredentialType) obj;
        final List<String> listToCheck = new ArrayList<>(ct2.attributes.keySet());
        return this.hasAttributes(listToCheck)
                && this.getIssuer().equals(ct2.getIssuer());
    }


    public String getProviderID() {
        return this.issuer.getProviderID();
    }
}
