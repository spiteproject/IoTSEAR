package be.distrinet.spite.iotsear.core.model.context.proof;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.model.Subject;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public abstract class OwnershipProof extends ContextProof implements ProviderSPI {
    private Subject subject;

    public OwnershipProof() {
    }

    public OwnershipProof(ContextAttribute attribute){
        super(attribute);
        setSubject(attribute.getSubject());
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setAttribute(ContextAttribute attribute) {
        super.setAttribute(attribute);
        setSubject(attribute.getSubject());
    }

    public abstract OwnershipProof getOwnershipProof(String proofString, ContextAttribute attribute);

    public abstract OwnershipProof createOwnershipProof(ContextAttribute attribute);
}
