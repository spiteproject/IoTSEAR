package be.distrinet.spite.iotsear.policy;

import be.distrinet.spite.iotsear.pbms.ContextStorage;

public interface PolicyConditionSet {
    PolicyConditionEvaluationResult evaluate(ContextStorage storage, final AuthorizationPolicy policy);
}
