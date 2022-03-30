package be.distrinet.spite.iotsear.policy;

import be.distrinet.spite.iotsear.core.exceptions.PolicyDeniedException;
import be.distrinet.spite.iotsear.pbms.ContextStorage;

public class AuthorizationPolicy {
    private final String identifier;
    private final PolicyEffect effect;
    private final PolicyTarget target;
    private PolicyTarget pepTarget;
    private final PolicyConditionSet condition;
    private int priority = -1;
    private PolicyConditionSet preCondition;
    private PolicyConditionEvaluationResult evaluationResult;

    public AuthorizationPolicy(final String identifier, final int priority, final PolicyTarget target, final PolicyEffect effect, final PolicyConditionSet condition) {
        this(identifier, target, effect, condition);
        this.priority = priority;

    }

    public AuthorizationPolicy(final String identifier, final int priority, final PolicyTarget target, final PolicyEffect effect, final PolicyConditionSet condition, final PolicyConditionSet preCondition) {
        this(identifier, priority, target, effect, condition);
        this.preCondition = preCondition;
    }

    public AuthorizationPolicy(final String identifier, final PolicyTarget target, final PolicyEffect effect, final PolicyConditionSet condition) {
        this.target = target;
        this.condition = condition;
        this.effect = effect;
        this.identifier = identifier;
    }

    public void enforce() throws PolicyDeniedException {
        if (this.effect == PolicyEffect.DENY) {
            throw new PolicyDeniedException();
        }
    }

    public PolicyConditionEvaluationResult evaluateCondition(final ContextStorage store) {
        final PolicyConditionEvaluationResult result = this.condition.evaluate(store, this);
        this.setEvaluationResult(result);
        return result;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public PolicyEffect getEffect() {
        return this.effect;
    }

    public int getPriority() {
        return this.priority;
    }

    public PolicyTarget getTarget() {
        return this.target;
    }

    public PolicyConditionSet getCondition() {
        return this.condition;
    }

    public PolicyConditionSet getPreCondition() {
        return this.preCondition;
    }

    public PolicyConditionEvaluationResult getEvaluationResult() {
        return this.evaluationResult;
    }

    public void setEvaluationResult(final PolicyConditionEvaluationResult evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public PolicyTarget getPepTarget() {
        return pepTarget;
    }

    public void setPepTarget(PolicyTarget pepTarget) {
        this.pepTarget = pepTarget;
    }

    public enum PolicyEffect {
        ALLOW, DENY
    }
}
