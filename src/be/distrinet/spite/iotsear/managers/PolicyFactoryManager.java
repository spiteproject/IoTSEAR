package be.distrinet.spite.iotsear.managers;

import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.policy.PolicyCondition;
import be.distrinet.spite.iotsear.policy.PolicyConditionOperation;
import be.distrinet.spite.iotsear.policy.PolicyConditionSet;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionOperationFactory;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionSetFactory;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionVerifierFactory;
import com.google.common.flogger.FluentLogger;
import org.pf4j.PluginManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyFactoryManager {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static PolicyFactoryManager INSTANCE;
    private final PluginManager pluginManager;
    private Map<String, be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionFactory> policyConditionFactories;
    private Map<String, PolicyConditionOperationFactory> policyConditionOperationFactories;
    private Map<String, PolicyConditionVerifierFactory> policyConditionVerifierFactories;
    private Map<String, PolicyConditionSetFactory> policyConditionSetFactories;

    private PolicyFactoryManager() {
        this.pluginManager = IoTSEAR.getInstance().getPluginManager();
        init();
    }

    public static PolicyFactoryManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PolicyFactoryManager();
        }
        return INSTANCE;
    }

    public void init() {
        this.policyConditionFactories = new HashMap<>();
        this.policyConditionOperationFactories = new HashMap<>();
        this.policyConditionVerifierFactories = new HashMap<>();
        this.policyConditionSetFactories = new HashMap<>();

        for (final be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionFactory provider : this.pluginManager.getExtensions(be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionFactory.class)) {
            logger.atInfo().log("Loading PolicyCondition factory: %s", provider.getProviderID());
            this.policyConditionFactories.put(provider.getProviderID(), provider);
        }

        for (final PolicyConditionOperationFactory provider : this.pluginManager.getExtensions(PolicyConditionOperationFactory.class)) {
            logger.atInfo().log("Loading PolicyConditionOperation factory: %s", provider.getProviderID());
            this.policyConditionOperationFactories.put(provider.getProviderID(), provider);
        }

        for (final PolicyConditionVerifierFactory provider : this.pluginManager.getExtensions(PolicyConditionVerifierFactory.class)) {
            logger.atInfo().log("Loading PolicyConditionVerifier factory: %s", provider.getProviderID());
            this.policyConditionVerifierFactories.put(provider.getProviderID(), provider);
        }
        for (final PolicyConditionSetFactory provider : this.pluginManager.getExtensions(PolicyConditionSetFactory.class)) {
            logger.atInfo().log("Loading PolicyConditionVerifier factory: %s", provider.getProviderID());
            this.policyConditionSetFactories.put(provider.getProviderID(), provider);
        }
    }

    public PolicyConditionSet createPolicyConditionSet(final String providerID, final List<PolicyConditionSet> policyConditions) throws ProviderNotFoundException {
        if (!this.policyConditionSetFactories.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.policyConditionSetFactories.get(providerID).createPolicyConditionSet(policyConditions);
    }

    public PolicyCondition createPolicyCondition(final String providerID,
                                                 final HashMap<String, Object> elements) throws ProviderNotFoundException {
        if (!this.policyConditionFactories.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.policyConditionFactories.get(providerID).createPolicyCondition(elements);
    }

    public PolicyConditionOperation createPolicyConditionOperation(final String providerID, final Map<String, String> parameters) throws ProviderNotFoundException {
        if (!this.policyConditionOperationFactories.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.policyConditionOperationFactories.get(providerID).createPolicyConditionOperation(parameters);
    }

    public PolicyConditionVerifier createPolicyConditionVerifier(final String providerID) throws ProviderNotFoundException {
        if (!this.policyConditionVerifierFactories.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.policyConditionVerifierFactories.get(providerID).createPolicyConditionVerifier();
    }


}
