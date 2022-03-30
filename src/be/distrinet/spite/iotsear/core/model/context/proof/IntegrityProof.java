package be.distrinet.spite.iotsear.core.model.context.proof;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public abstract class IntegrityProof extends ContextProof implements ProviderSPI {




    public IntegrityProof() {
    }
    public IntegrityProof(ContextAttribute attribute){
        super(attribute);
    }

    public abstract IntegrityProof getIntegrityProof(String proofString, ContextAttribute attribute);

    public abstract IntegrityProof createIntegrityProof(ContextAttribute attribute);
}
