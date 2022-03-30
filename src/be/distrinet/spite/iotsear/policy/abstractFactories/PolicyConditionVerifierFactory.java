package be.distrinet.spite.iotsear.policy.abstractFactories;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;

public interface PolicyConditionVerifierFactory extends ProviderSPI {
    PolicyConditionVerifier createPolicyConditionVerifier();
}
