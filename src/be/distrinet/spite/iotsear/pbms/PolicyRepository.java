package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import org.json.simple.JSONObject;

import java.util.List;

public interface PolicyRepository extends ProviderSPI {

    /**
     * Retrieve a specific policy
     *
     * @param policyID the identifier of the desired policy
     * @return the policy, or null if it is not found
     */
    AuthorizationPolicy retrievePolicy(String policyID);

    /**
     * Retrieve the list of all policy-identifiers in this repository
     */
    List<String> getAllPolicyIDs();

    /**
     * Retrieve all policies in this repository
     *
     * @return the list of all policies in this repository
     */
    List<AuthorizationPolicy> retrievePolicies();

    /**
     * Store a new policy in this repository.
     * This operation will not succeed if a policy with the same identifier is already present (see #storePolicy)
     *
     * @param policy the policy
     * @return true if the operation succeeds, false otherwise.
     */
    boolean storePolicy(AuthorizationPolicy policy);

    /**
     * Update an existing policy in this repository.
     * This operation will not succeed if this repository does not already contain a policy with the same identifier.
     *
     * @param policy the policy
     * @return true if the operation succeeds, false otherwise.
     */
    boolean updatePolicy(AuthorizationPolicy policy);

    /**
     * Delete an existing policy in this repository
     *
     * @param policyID the identifier of the policy that should be deleted
     * @return true if the operation succeeds, false otherwise.
     */
    boolean deletePolicy(String policyID);

    /**
     * Create and initialize a new policyrepository using the given configuration.
     * This is an internal (framework) method and should not be used.
     *
     * @param config
     * @return
     */
    PolicyRepository init(JSONObject config);
}
