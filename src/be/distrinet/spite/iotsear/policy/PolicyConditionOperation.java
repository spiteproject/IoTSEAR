package be.distrinet.spite.iotsear.policy;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

import java.util.HashMap;
import java.util.Map;

public abstract class PolicyConditionOperation {
    protected final Map<String, String> parameters;

    public PolicyConditionOperation(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public PolicyConditionOperation() {
        this.parameters = new HashMap<>();
    }

    public abstract boolean match(String source, String value, ContextAttribute attribute, final AuthorizationPolicy policy);
}
