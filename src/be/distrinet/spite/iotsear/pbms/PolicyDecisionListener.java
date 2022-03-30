package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyTarget;

import java.util.List;

public abstract class PolicyDecisionListener {
    /**
     * This method will be called by the PolicyEngine with the result of the policy-decision.
     * The PEP must act according to the provided policyEffect!
     *
     * @param effect    the policy effect, which is the result of an access control query
     * @param pepTarget the target that was passed to the PolicyEngine by the pep in the original access control query
     */
    public abstract void processDecision(AuthorizationPolicy.PolicyEffect effect, final PolicyTarget pepTarget);

    /**
     * This method will be called by the PolicyEngine with the policy that must be enforced (the one that matched with the highest priority).
     * This method must be explicitly overridden by the implementing class, otherwise it will not do anything.
     *
     * @param policy    the policy that was satisfied with the highest priority
     * @param pepTarget the target that was passed to the PolicyEngine by the pep in the original access control query
     */
    public void notifyEnforcedPolicy(final AuthorizationPolicy policy, final PolicyTarget pepTarget) {

    }

    /**
     * This method will be called by the PolicyEngine with the list of all policies that were satisfied.
     * This method must be explicitly overridden by the implementing class, otherwise it will not do anything.
     *
     * @param policies  the list of all policies that were satisfied.
     * @param pepTarget the target that was passed to the PolicyEngine by the pep in the original access control query
     */
    public void notifyMatchedPolicies(final List<AuthorizationPolicy> policies, final PolicyTarget pepTarget) {

    }
}
