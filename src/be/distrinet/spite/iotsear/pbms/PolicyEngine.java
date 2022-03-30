package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;
import be.distrinet.spite.iotsear.policy.PolicyTarget;
import be.distrinet.spite.iotsear.systemProviders.pbms.CascadeRepository;
import be.distrinet.spite.iotsear.systemProviders.pbms.CascadeStore;
import com.google.common.flogger.FluentLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolicyEngine {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final PolicyRepository repository;
    private final PDP pdp;
    private final ContextStorage store;

    public PolicyEngine(final PDP pdp, final PolicyRepository repository, final ContextStorage contextStore) {
        this.pdp = pdp;
        this.repository = repository;
        this.store = contextStore;
    }

    public PolicyEngine(final PDP pdp, final List<PolicyRepository> repositories, final List<ContextStorage> stores) {
        this.pdp = pdp;
        this.repository = new CascadeRepository(repositories);
        this.store = new CascadeStore(stores);
    }

    /**
     * Asynchronous method
     *
     * Enforce the set of policy in this policy-engine's repository.
     * This method will return a default PolicyEffect.DENY if no policy matches.
     *
     * @param pepTarget the subject/resource/action of the request. This corresponds with the policy-target.
     * @param listener  the listener that will be notified with the policy-decision.
     */
    public void enforce(final PolicyTarget pepTarget, final PolicyDecisionListener listener) {
        final List<AuthorizationPolicy> policies = this.repository.retrievePolicies();
        final List<AuthorizationPolicy> filtered = new ArrayList<>();
        for (final AuthorizationPolicy policy : policies) {
            final PolicyTarget target = policy.getTarget();
            if (target.getSubject().equals(pepTarget.getSubject()) || target.getSubject().equalsIgnoreCase("anysubject")) {
                if (target.getAction().equals(pepTarget.getAction()) || target.getAction().equalsIgnoreCase("anyaction")) {
                    if (target.getResource().equals(pepTarget.getResource()) || target.getResource().equalsIgnoreCase("anyresource")) {
                        policy.setPepTarget(pepTarget);
                        filtered.add(policy);
                    }
                }
            }
        }

        Collections.sort(filtered, Comparator.comparingInt(AuthorizationPolicy::getPriority));
        final ArrayList<AuthorizationPolicy> matchedPolicies = new ArrayList<>();
        {
            for (final AuthorizationPolicy pol : filtered) {
                final PolicyConditionEvaluationResult result = this.pdp.evaluate(pol, this.store);
                pol.setEvaluationResult(result);
                if (result.isTrue()) {
                    if (matchedPolicies.isEmpty()) {
                        new Thread(() -> listener.processDecision(pol.getEffect(), pepTarget)).start();
                        new Thread(() -> listener.notifyEnforcedPolicy(pol, pepTarget)).start();
                    }
                    matchedPolicies.add(pol);
                }
            }
        }
        if (matchedPolicies.size() < 1) {
            logger.atInfo().log("No policies satisfied --> Default DENY");
            listener.processDecision(AuthorizationPolicy.PolicyEffect.DENY, pepTarget);
        } else {
            final StringBuilder sb = new StringBuilder();
            matchedPolicies.forEach(authorizationPolicy -> sb.append(authorizationPolicy.getIdentifier() + ", "));
            logger.atInfo().log("In total %d satisfied policies: %s", matchedPolicies.size(), sb.toString());
            listener.notifyMatchedPolicies(matchedPolicies, pepTarget);
        }
    }


    /**
     * Asynchronous method
     *
     * Enforce the set of policy in this policy-engine's repository.
     * This method will return a default PolicyEffect.DENY if no policy matches.
     *
     * @param pepTarget the subject/resource/action of the request. This corresponds with the policy-target.
     * @return A PolicyEffect (ACCEPT/DENY)
     */
    public AuthorizationPolicy.PolicyEffect enforce(final PolicyTarget pepTarget) {
        //FIXME code duplication
        final List<AuthorizationPolicy> policies = this.repository.retrievePolicies();
        final List<AuthorizationPolicy> filtered = new ArrayList<>();
        for (final AuthorizationPolicy policy : policies) {
            final PolicyTarget target = policy.getTarget();
            if (target.getSubject().equals(pepTarget.getSubject()) || target.getSubject().equalsIgnoreCase("anysubject")) {
                if (target.getAction().equals(pepTarget.getAction()) || target.getAction().equalsIgnoreCase("anyaction")) {
                    if (target.getResource().equals(pepTarget.getResource()) || target.getResource().equalsIgnoreCase("anyresource")) {
                        policy.setPepTarget(pepTarget);
                        filtered.add(policy);
                    }
                }
            }
        }

        Collections.sort(filtered, Comparator.comparingInt(AuthorizationPolicy::getPriority));
        final ArrayList<AuthorizationPolicy> matchedPolicies = new ArrayList<>();
        {
            for (final AuthorizationPolicy pol : filtered) {
                final PolicyConditionEvaluationResult result = this.pdp.evaluate(pol, this.store);
                pol.setEvaluationResult(result);
                if (result.isTrue()) {
                    logger.atInfo().log("Found satisfied policy: "+pol.getIdentifier());
                    return pol.getEffect();
                }
            }
        }
        logger.atInfo().log("No policies satisfied --> Default DENY");
        return AuthorizationPolicy.PolicyEffect.DENY;

    }


}
