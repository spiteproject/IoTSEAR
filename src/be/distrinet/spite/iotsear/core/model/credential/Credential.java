package be.distrinet.spite.iotsear.core.model.credential;

import be.distrinet.spite.iotsear.core.exceptions.VerificationException;
import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.Subject;
import be.distrinet.spite.iotsear.core.model.credential.issuance.Issuer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Credential {

    /**
     * the list of attributes of this credential
     */
    private final List<Attribute> attributes;
    private final CredentialType credentialType;
    /**
     * the secret which is required to prove ownership of this credential
     */
    private Secret secret;
    /**
     * the subject/entity to which this credential belongs
     */
    private Subject subject;


    /**
     * Constructor for a Credential.
     *
     * @param attributes     the list of attributes
     * @param credentialType the type of this credential
     */
    public Credential(final List<Attribute> attributes, final CredentialType credentialType) {
        this.attributes = attributes;
        this.credentialType = credentialType;
    }


    /**
     * Constructor for a Credential.
     *
     * @param attributes the list of attributes
     * @param issuer     the issuer of this credential
     * @param secret     the credential's secret
     */
    public Credential(final List<Attribute> attributes, final Issuer issuer, final Secret secret) {
        this(attributes, issuer);
        this.secret = secret;
    }

    /**
     * Constructor for a Credential.
     *
     * @param attributes the list of attributes
     * @param issuer     the issuer of this credential
     */
    public Credential(final List<Attribute> attributes, final Issuer issuer) {
        this(attributes, new CredentialType(Credential.getAttributeMap(attributes), issuer));
    }


    /**
     * Constructor for a Credential.
     *
     * @param attributes     the list of attributes
     * @param credentialType the type of this credential
     * @param secret         the credential's secret
     */
    public Credential(final List<Attribute> attributes, final CredentialType credentialType, final Secret secret) {
        this(attributes, credentialType);
        this.secret = secret;
    }

    private static Map<String, String> getAttributeMap(final List<Attribute> attributes) {
        final Map<String, String> theMap = new HashMap<>();
        for (final Attribute att : attributes) {
            theMap.put(att.getType(), "string");
        }
        return theMap;
    }


    /**
     * @param attributeName the name, or label, of the attribute
     * @return an attribute which has the desired name, or null if
     * this credential does not have such an attribute.
     */
    public Attribute getAttribute(final String attributeName) {
        for (final Attribute att : this.attributes) {
            if (att.getType().equals(attributeName)) {
                return att;
            }
        }
        return null;
    }

    public Subject getSubject() {

        return this.subject;
    }

    public void setSubject(final Subject subject) {
        this.subject = subject;
    }

    public Secret getSecret() {
        return this.secret;
    }

    public CredentialType getCredentialType() {
        return this.credentialType;
    }

    /**
     * Verifies the integrity/authenticity of this credential
     * If the verification fails, a VerificationException (-> one of its subtypes) will be thrown.
     * The type of exception indicates the reason why the verification failed.
     */
    public abstract void verify() throws VerificationException;

    public abstract boolean checkSecret(Secret secret);


}
