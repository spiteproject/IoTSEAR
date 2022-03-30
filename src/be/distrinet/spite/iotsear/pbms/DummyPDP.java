package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;

import java.util.ArrayList;

public class DummyPDP extends PDP{
    @Override
    public PolicyConditionEvaluationResult evaluate(AuthorizationPolicy policy, ContextStorage storage) {
        return new PolicyConditionEvaluationResult(false,new ArrayList<>());
    }

    @Override
    public String getProviderID() {
        return "iotsear:pbms:dummyPDP";
    }
}
