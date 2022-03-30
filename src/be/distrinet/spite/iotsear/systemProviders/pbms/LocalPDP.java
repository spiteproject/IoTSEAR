package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.pbms.ContextStorage;
import be.distrinet.spite.iotsear.pbms.PDP;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;
import org.pf4j.Extension;

@Extension
public class LocalPDP extends PDP {

    @Override
    public String getProviderID() {
        return "iotsear:pbms:localPDP";
    }


    @Override
    public PolicyConditionEvaluationResult evaluate(final AuthorizationPolicy policy, final ContextStorage storage) {
        return policy.evaluateCondition(storage);
    }
}
