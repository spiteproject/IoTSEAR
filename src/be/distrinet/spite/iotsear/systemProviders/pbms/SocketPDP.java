package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.pbms.ContextStorage;
import be.distrinet.spite.iotsear.pbms.PDP;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionEvaluationResult;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

@Extension
public class SocketPDP extends PDP {
    @Override
    public String getProviderID() {
        return "iotsear:pbms:socketPDP";
    }

    @Override
    public void setConfiguration(JSONObject configuration) {

    }

    @Override
    public PolicyConditionEvaluationResult evaluate(final AuthorizationPolicy policy, final ContextStorage storage) {
        return policy.evaluateCondition(storage);
    }

}
