package be.distrinet.spite.iotsear.policy.abstractFactories;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;

import java.util.Map;

public interface PolicyConditionOperationFactory extends ProviderSPI {
    PolicyConditionOperation createPolicyConditionOperation(Map<String, String> arguments);
}
