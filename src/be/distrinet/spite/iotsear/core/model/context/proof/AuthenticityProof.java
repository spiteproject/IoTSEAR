package be.distrinet.spite.iotsear.core.model.context.proof;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;

public abstract class AuthenticityProof extends IntegrityProof implements ProviderSPI {


    public AuthenticityProof() {
        super();
    }
    public AuthenticityProof(ContextAttribute attribute){
        super(attribute);
    }

    public ContextSource getSource() {
        return this.attribute.getSource();
    }

    public ContextAttribute getAttribute() {
        return this.attribute;
    }

    public abstract AuthenticityProof getAuthenticityProof(String proofString, ContextAttribute attribute);

    @Override
    public IntegrityProof getIntegrityProof(String proofString, ContextAttribute attribute) {
        return getAuthenticityProof(proofString, attribute);
    }
    public abstract AuthenticityProof createAuthenticityProof(ContextSource source, ContextAttribute attribute) throws InvalidSignatureException, ProviderNotFoundException;

    @Override
    public IntegrityProof createIntegrityProof(ContextAttribute attribute) {
        try {
            return createAuthenticityProof(attribute.getSource(),attribute);
        } catch (InvalidSignatureException e) {
            e.printStackTrace();
        } catch (ProviderNotFoundException e) {
            e.printStackTrace();
        }return null;
    }
}
