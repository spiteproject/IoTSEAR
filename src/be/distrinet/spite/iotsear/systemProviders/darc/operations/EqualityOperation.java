package be.distrinet.spite.iotsear.systemProviders.darc.operations;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionOperationFactory;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class EqualityOperation extends PolicyConditionOperation implements PolicyConditionOperationFactory {


    @Override
    public PolicyConditionOperation createPolicyConditionOperation(final Map<String, String> arguments) {
        return new EqualityOperation();
    }

    @Override
    public String getProviderID() {
        return "darc:condition:operation:equals";
    }

    @Override
    public boolean match(final String source, final String value, final ContextAttribute attribute, final AuthorizationPolicy policy) {
        return value.equals(attribute.getValue());
    }
}
