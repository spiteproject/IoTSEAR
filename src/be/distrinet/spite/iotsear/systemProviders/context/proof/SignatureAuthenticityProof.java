package be.distrinet.spite.iotsear.systemProviders.context.proof;

import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import be.distrinet.spite.iotsear.core.model.context.proof.AuthenticityProof;
import be.distrinet.spite.iotsear.crypto.SignatureSPI;
import be.distrinet.spite.iotsear.managers.CryptoManager;
import org.pf4j.Extension;

@Extension
public class SignatureAuthenticityProof extends AuthenticityProof {
    private SignatureSPI signer;
    public SignatureAuthenticityProof(){}

    public SignatureAuthenticityProof(ContextSource proofOrigin, ContextAttribute attribute) throws InvalidSignatureException, ProviderNotFoundException {
        super(attribute);
        String providerString = proofOrigin.getMetaData("authenticity");
        if(providerString == null) throw new RuntimeException("ContextSource "+ proofOrigin.getIdentifier()+" must have the authenticity metadata attribute set");
        signer = CryptoManager.getInstance().getSignatureProvider(providerString);
        proofString = signer.sign(getData());
        attribute.setAuthenticityProof(this);
    }

    private String getData() {
        return getAttribute().getValue() + getAttribute().getTimestamp();
    }

    @Override
    public AuthenticityProof getAuthenticityProof(String proofString, ContextAttribute attribute) {
        SignatureAuthenticityProof ap = new SignatureAuthenticityProof();
        ap.proofString = proofString;
        ap.setAttribute(attribute);
        try {
            ap.signer = CryptoManager.getInstance().getSignatureProvider(attribute.getMetaData(ContextAttribute.APP));
        } catch (ProviderNotFoundException e) {
            e.printStackTrace();
        }
        return ap;
    }

    @Override
    public AuthenticityProof createAuthenticityProof(ContextSource source, ContextAttribute attribute) throws InvalidSignatureException, ProviderNotFoundException {
        return new SignatureAuthenticityProof(source, attribute);
    }

    @Override
    public String getProviderID() {
        return "iotsear:context:proof:generic-authenticity-proof";
    }

    @Override
    public boolean _verify() {
        try {
            return getProofString() != null && signer.verify(getData(), this.getProofString());
        } catch (InvalidSignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

}
