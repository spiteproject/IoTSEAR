package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.pbms.PolicyRepository;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CascadeRepository implements PolicyRepository {
    private final List<PolicyRepository> repositories;

    public CascadeRepository(final List<PolicyRepository> policyRepositories) {
        this.repositories = policyRepositories;
    }

    @Override
    public AuthorizationPolicy retrievePolicy(final String policyID) {
        for (final PolicyRepository repo : this.repositories) {
            final AuthorizationPolicy policy = repo.retrievePolicy(policyID);
            if (policy != null) {
                return policy;
            }
        }
        return null;
    }

    @Override
    public List<String> getAllPolicyIDs() {
        final List<String> list = new ArrayList<>();
        for (final PolicyRepository repo : this.repositories) {
            list.addAll(repo.getAllPolicyIDs());
        }
        return list;
    }

    @Override
    public List<AuthorizationPolicy> retrievePolicies() {
        final List<AuthorizationPolicy> list = new ArrayList<>();
        for (final PolicyRepository repo : this.repositories) {
            list.addAll(repo.retrievePolicies());
        }
        return list;
    }

    @Override
    public boolean storePolicy(final AuthorizationPolicy policy) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean updatePolicy(final AuthorizationPolicy policy) {
        boolean success = false;
        for (final PolicyRepository repo : this.repositories) {
            success = success || repo.updatePolicy(policy);
        }
        return success;
    }

    @Override
    public boolean deletePolicy(final String policyID) {
        boolean success = false;
        for (final PolicyRepository repo : this.repositories) {
            success = success || repo.deletePolicy(policyID);
        }
        return success;
    }

    @Override
    public PolicyRepository init(final JSONObject config) {
        return null;
    }

    @Override
    public String getProviderID() {
        return null;
    }
}
