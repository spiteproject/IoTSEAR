package be.distrinet.spite.iotsear.core.model.context.proof;

import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;

public abstract class ContextProof {

    protected ContextAttribute attribute;
    protected String proofString = "";
    private boolean verified = false;
    private boolean verificationResult = false;

    public ContextAttribute getAttribute() {
        return this.attribute;
    }
    public void setAttribute(ContextAttribute attribute) {
        this.attribute = attribute;
    }

    public ContextProof() {
    }
    public ContextProof(ContextAttribute attribute){
        this.attribute = attribute;
    }

    public boolean verify(){
        if(!verified){
            verificationResult = _verify();
            verified = true;
        }
        return verificationResult;
    }

    protected abstract boolean _verify();

    public String getProofString() {
        return proofString;
    }

}
