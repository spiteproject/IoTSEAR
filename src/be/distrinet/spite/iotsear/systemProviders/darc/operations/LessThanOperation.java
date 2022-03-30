package be.distrinet.spite.iotsear.systemProviders.darc.operations;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionOperationFactory;
import com.google.common.flogger.FluentLogger;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class LessThanOperation extends PolicyConditionOperation implements PolicyConditionOperationFactory {
    @Override
    public PolicyConditionOperation createPolicyConditionOperation(final Map<String, String> arguments) {
        return new LessThanOperation();
    }

    @Override
    public String getProviderID() {
        return "darc:condition:operation:lt";
    }


    @Override
    public boolean match(final String source, final String value, final ContextAttribute attribute, final AuthorizationPolicy policy) {
        try {
            return Long.parseLong(attribute.getValue()) < Long.parseLong(value);
        } catch (final Exception e) {
            try {
                return Double.parseDouble(attribute.getValue()) < Double.parseDouble(value);
            } catch (final Exception e2) {
                FluentLogger.forEnclosingClass().atWarning().withCause(e2).log("Cannot parse value for inequality operation");
                return false;
            }
        }
    }
}
