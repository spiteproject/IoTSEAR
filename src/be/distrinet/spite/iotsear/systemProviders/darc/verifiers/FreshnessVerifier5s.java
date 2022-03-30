package be.distrinet.spite.iotsear.systemProviders.darc.verifiers;

import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionVerifierFactory;
import org.pf4j.Extension;

@Extension
public class FreshnessVerifier5s extends FreshnessVerifier implements PolicyConditionVerifierFactory {
    @Override
    public long getTimeDiff() {
        return 5000;
    }

    @Override
    public String getProviderID() {
        return "darc:condition:verifier:freshness:5s";
    }

    @Override
    public PolicyConditionVerifier createPolicyConditionVerifier() {
        return new FreshnessVerifier5s();
    }
}
