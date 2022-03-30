package be.distrinet.spite.iotsear.systemProviders.darc.verifiers;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;

public abstract class FreshnessVerifier extends PolicyConditionVerifier {
    @Override
    public boolean verify(final ContextAttribute attribute, final AuthorizationPolicy policy) {
        final long timestamp = attribute.getTimestamp();
        long time = System.currentTimeMillis();
        return time - timestamp < getTimeDiff() && time > timestamp;
    }
    public abstract long getTimeDiff();
}

