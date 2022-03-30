package be.distrinet.spite.iotsear.systemProviders.darc.operations;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionOperationFactory;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class ContainsOperation extends PolicyConditionOperation implements PolicyConditionOperationFactory {
    @Override
    public String getProviderID() {
        return "darc:condition:operation:contains";
    }

    @Override
    public boolean match(String source, String value, ContextAttribute attribute, final AuthorizationPolicy policy) {
        return attribute.getValue().contains(value.trim());
    }

    @Override
    public PolicyConditionOperation createPolicyConditionOperation(Map<String, String> arguments) {
        return new ContainsOperation();
    }
}
