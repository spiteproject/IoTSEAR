package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;
import org.json.simple.JSONObject;

public abstract class PDP implements ProviderSPI {
    /**
     * Initialize this PDP with the given configuration object
     *
     * IMPORTANT: the default implementation is empty. Subclasses that need to be initialized with a
     * particular configuration must explicitly override this method
     * @param configuration
     */
    public void setConfiguration(JSONObject configuration){}

    /**
     * Performs policy evaluation
     * @param policy the policy that is evaluated
     * @param storage the ContextStore that contains the relevan context attributes
     * @return the PolicyConditionEvaluationResult
     */
    public abstract PolicyConditionEvaluationResult evaluate(AuthorizationPolicy policy, ContextStorage storage);
}
