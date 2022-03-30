package be.distrinet.spite.iotsear.policy.abstractFactories;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.policy.PolicyConditionSet;

import java.util.List;

public interface PolicyConditionSetFactory extends ProviderSPI {
    PolicyConditionSet createPolicyConditionSet(List<PolicyConditionSet> conditions);
}
