package be.distrinet.spite.iotsear.policy.abstractFactories;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.policy.PolicyCondition;

import java.util.HashMap;

public interface PolicyConditionFactory extends ProviderSPI {
    PolicyCondition createPolicyCondition(final HashMap<String, Object> policyParts) throws ProviderNotFoundException;
}
