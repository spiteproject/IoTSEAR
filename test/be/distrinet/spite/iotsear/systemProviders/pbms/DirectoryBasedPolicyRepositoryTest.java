package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.pbms.PolicyRepository;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


class DirectoryBasedPolicyRepositoryTest {
    PolicyRepository repo = new DirectoryBasedPolicyRepository();

    @Before
    @Test
    void init() {
        final JSONObject json = new JSONObject();
        json.put("cache", true);
        json.put("path", "./testPolicies");
        this.repo = this.repo.init(json);
        assertTrue(this.repo.getAllPolicyIDs().size() >= 3);
    }

    @Test
    void retrievePolicy() {
        assertNotNull(this.repo.retrievePolicy("myPolicy3"));
        assertNotNull(this.repo.retrievePolicy("myPolicy2"));
        assertNotNull(this.repo.retrievePolicy("myPolicy"));
    }

    @Test
    void getAllPolicyIDs() {
        final List<String> policyIds = this.repo.getAllPolicyIDs();
        assertTrue(policyIds.contains("myPolicy"));
        assertTrue(policyIds.contains("myPolicy2"));
        assertTrue(policyIds.contains("myPolicy3"));
    }


}