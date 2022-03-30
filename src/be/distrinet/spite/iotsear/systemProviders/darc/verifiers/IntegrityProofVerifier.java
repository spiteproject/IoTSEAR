package be.distrinet.spite.iotsear.systemProviders.darc.verifiers;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.proof.ContextProof;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionVerifierFactory;
import org.pf4j.Extension;

@Extension
public class IntegrityProofVerifier extends PolicyConditionVerifier implements PolicyConditionVerifierFactory {
    @Override
    public String getProviderID() {
        return "darc:condition:verifier:integrity";
    }

    @Override
    public boolean verify(ContextAttribute attribute, AuthorizationPolicy policy) {
        ContextProof proof = attribute.getIntegrityProofFromMetadata();
        if(proof == null) proof = attribute.getAuthenticityProofFromMetadata();
        if(proof == null) proof = attribute.getOwnershipProofFromMetadata();
        return proof != null && proof.verify();
    }

    @Override
    public PolicyConditionVerifier createPolicyConditionVerifier() {
        return new IntegrityProofVerifier();
    }
}
