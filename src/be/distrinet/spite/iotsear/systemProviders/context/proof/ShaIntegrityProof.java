package be.distrinet.spite.iotsear.systemProviders.context.proof;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.proof.IntegrityProof;
import be.distrinet.spite.iotsear.crypto.HashSPI;
import be.distrinet.spite.iotsear.systemProviders.crypto.SHA256;
import org.pf4j.Extension;

@Extension
public class ShaIntegrityProof extends IntegrityProof {
    private HashSPI hasher = new SHA256();

    /**
     * constructor for proof-verification
     */
    public ShaIntegrityProof(){}

    /**
     * constructor for proof-validation
     * @param attribute
     */
    public ShaIntegrityProof(ContextAttribute attribute) {
        super(attribute);
        proofString = hasher.doHash(getAttribute().getValue());
        attribute.setIntegrityProof(this);
    }

    @Override
    public boolean _verify() {
        return proofString != null && proofString.equals(hasher.doHash(getAttribute().getValue()));
    }



    @Override
    public IntegrityProof getIntegrityProof(String proofString, ContextAttribute attribute) {
        ShaIntegrityProof ip = new ShaIntegrityProof();
        ip.proofString = proofString;
        ip.setAttribute(attribute);
        return ip;
    }

    @Override
    public IntegrityProof createIntegrityProof(ContextAttribute attribute) {
        return new ShaIntegrityProof(attribute);
    }

    @Override
    public String getProviderID() {
        return "iotsear:context:proof:sha-integrity-proof";
    }



}
