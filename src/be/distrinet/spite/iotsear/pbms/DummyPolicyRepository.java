package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DummyPolicyRepository implements PolicyRepository {
    @Override
    public AuthorizationPolicy retrievePolicy(String policyID) {
        return null;
    }

    @Override
    public List<String> getAllPolicyIDs() {
        return new ArrayList<>();
    }

    @Override
    public List<AuthorizationPolicy> retrievePolicies() {
        return new ArrayList<>();
    }

    @Override
    public boolean storePolicy(AuthorizationPolicy policy) {
        return false;
    }

    @Override
    public boolean updatePolicy(AuthorizationPolicy policy) {
        return false;
    }

    @Override
    public boolean deletePolicy(String policyID) {
        return false;
    }

    @Override
    public PolicyRepository init(JSONObject config) {
        return new DummyPolicyRepository();
    }

    @Override
    public String getProviderID() {
        return "iotsear:pbms:repository:dummy";
    }
}
