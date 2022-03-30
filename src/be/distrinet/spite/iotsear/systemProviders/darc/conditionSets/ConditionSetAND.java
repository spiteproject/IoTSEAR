package be.distrinet.spite.iotsear.systemProviders.darc.conditionSets;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.pbms.ContextStorage;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;
import be.distrinet.spite.iotsear.policy.PolicyConditionSet;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionSetFactory;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.List;

@Extension
public class ConditionSetAND implements PolicyConditionSet, PolicyConditionSetFactory {

    private final List<PolicyConditionSet> subConditions;

    public ConditionSetAND() {
        this.subConditions = new ArrayList<>();
    }

    public ConditionSetAND(final List<PolicyConditionSet> subConditions) {
        if (subConditions.size() < 2) {
            throw new IllegalArgumentException("A policy condition set must have at least 1 subcondition");
        }
        this.subConditions = subConditions;
    }


    @Override
    public String getProviderID() {
        return "darc:condition:and";
    }

    @Override
    public PolicyConditionEvaluationResult evaluate(final ContextStorage storage, final AuthorizationPolicy policy) {
        final List<ContextAttribute> matchedAttributes = new ArrayList<>();
        for (final PolicyConditionSet condition : this.subConditions) {
            final PolicyConditionEvaluationResult evaluationResult = condition.evaluate(storage, policy);
            if (!evaluationResult.isTrue()) {
                return new PolicyConditionEvaluationResult(false, new ArrayList<>());
            } else {
                matchedAttributes.addAll(evaluationResult.getMatchedAttributes());
            }
        }
        return new PolicyConditionEvaluationResult(true, matchedAttributes);
    }

    @Override
    public PolicyConditionSet createPolicyConditionSet(final List<PolicyConditionSet> conditions) {
        return new ConditionSetAND(conditions);
    }
}
