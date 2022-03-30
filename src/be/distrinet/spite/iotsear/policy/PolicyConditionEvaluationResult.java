package be.distrinet.spite.iotsear.policy;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

import java.util.List;

public class PolicyConditionEvaluationResult {
    private final boolean result;
    private final List<ContextAttribute> matchedAttributes;

    public PolicyConditionEvaluationResult(final boolean result, final List<ContextAttribute> matchedAttributes) {
        this.result = result;
        this.matchedAttributes = matchedAttributes;
    }

    public PolicyConditionEvaluationResult(final List<ContextAttribute> matchedAttributes) {
        this(!matchedAttributes.isEmpty(), matchedAttributes);
    }


    /**
     * @return whether or not the policy was determined to be satisfied (during policy evaluation)
     */
    public boolean isTrue() {
        return this.result;
    }

    /**
     * @return a list of ContextAttributes that were used to satify the policy
     */
    public List<ContextAttribute> getMatchedAttributes() {
        return this.matchedAttributes;
    }
}
