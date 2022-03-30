package be.distrinet.spite.iotsear.systemProviders.darc.verifiers;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.proof.ContextProof;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionVerifierFactory;
import org.pf4j.Extension;

@Extension
public class AuthenticityProofVerifier extends PolicyConditionVerifier implements PolicyConditionVerifierFactory {
    @Override
    public String getProviderID() {
        return "darc:condition:verifier:authenticity";
    }

    @Override
    public boolean verify(ContextAttribute attribute, AuthorizationPolicy policy) {
        ContextProof proof = attribute.getAuthenticityProofFromMetadata();
        if(proof == null) proof = attribute.getOwnershipProofFromMetadata();
        return proof != null && proof.verify();
    }

    @Override
    public PolicyConditionVerifier createPolicyConditionVerifier() {
        return new AuthenticityProofVerifier();
    }
}
