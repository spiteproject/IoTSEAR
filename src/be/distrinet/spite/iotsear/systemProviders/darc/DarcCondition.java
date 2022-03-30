package be.distrinet.spite.iotsear.systemProviders.darc;

import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.managers.PolicyFactoryManager;
import be.distrinet.spite.iotsear.policy.PolicyCondition;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionFactory;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class DarcCondition extends PolicyCondition implements PolicyConditionFactory {

    public DarcCondition() {
    }

    public DarcCondition(final String source, final PolicyConditionOperation operation, final String value, final List<PolicyConditionVerifier> verifiers) {
        super();
        super.setSource(source);
        super.setOperation(operation);
        super.setValue(value);
        super.setVerifiers(verifiers);
    }

    public DarcCondition(final String source, final PolicyConditionOperation operation, final String value) {
        this(source, operation, value, new ArrayList<>());
    }

    @Override
    public String getProviderID() {
        return "darc:condition";
    }

    @Override
    public PolicyCondition createPolicyCondition(final HashMap<String, Object> policyParts) throws ProviderNotFoundException {
        final String source = policyParts.containsKey("source")?policyParts.get("source").toString():"";
        final PolicyConditionOperation operation = PolicyFactoryManager.getInstance().createPolicyConditionOperation(policyParts.get("operation").toString(), (Map<String, String>) policyParts.get("arguments"));

        final String value = policyParts.containsKey("value")? policyParts.get("value").toString():"";
        final List<String> verifierProviders = (List<String>) policyParts.get("verifiers");
        final List<PolicyConditionVerifier> verifiers = new ArrayList<>();
        for (final String provider : verifierProviders) {
            verifiers.add(PolicyFactoryManager.getInstance().createPolicyConditionVerifier(provider));
        }
        return new DarcCondition(source, operation, value, verifiers);
    }
}
