package be.distrinet.spite.iotsear.policy;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public abstract class PolicyConditionVerifier {
    public abstract boolean verify(ContextAttribute attribute, final AuthorizationPolicy policy);
}
