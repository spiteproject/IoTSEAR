package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.core.exceptions.ProviderException;
import be.distrinet.spite.iotsear.pbms.PolicyParser;
import be.distrinet.spite.iotsear.pbms.PolicyRepository;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.systemProviders.darc.DarcJSONParser;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Extension
public class DirectoryBasedPolicyRepository implements PolicyRepository {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    //TODO make darcjsonparser a provider
    private final PolicyParser parser = new DarcJSONParser();
    private final HashMap<String, AuthorizationPolicy> policyMap = new HashMap<>();
    private boolean cache = false;
    private Path path = Paths.get("./");

    @Override
    public AuthorizationPolicy retrievePolicy(final String policyID) {
        if (!this.cache) {
            this.readPolicies();
        }
        return this.policyMap.get(policyID);
    }

    @Override
    public List<String> getAllPolicyIDs() {

        if (!this.cache) {
            this.readPolicies();
        }
        return new ArrayList<>(this.policyMap.keySet());
    }

    @Override
    public List<AuthorizationPolicy> retrievePolicies() {

        if (!this.cache) {
            this.readPolicies();
        }
        return new ArrayList<>(this.policyMap.values());
    }

    @Override
    public boolean storePolicy(final AuthorizationPolicy policy) {

        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean updatePolicy(final AuthorizationPolicy policy) {
        if (this.path.resolve(policy.getIdentifier()).toFile().exists()) {
            return false;
        } else {
            return storePolicy(policy);
        }
    }

    @Override
    public boolean deletePolicy(final String policyID) {
        final File file = this.path.resolve(policyID).toFile();
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    @Override
    public PolicyRepository init(final JSONObject config) {
        this.logger.atInfo().log("initializing with config: \n" + config.toJSONString());
        final DirectoryBasedPolicyRepository newInstance = new DirectoryBasedPolicyRepository();
        if (config.containsKey("cache")) {
            newInstance.setCache((Boolean) config.get("cache"));
        }
        if (config.containsKey("path")) {
            newInstance.setPath(Paths.get(config.get("path").toString()));
        }
        newInstance.readPolicies();
        return newInstance;
    }

    @Override
    public String getProviderID() {
        return "iotsear:pbms:directory-based-policy-repository";
    }

    private void readPolicies() {
        this.policyMap.clear();
        final File policyDir = this.path.toFile();
        for (final File fileEntry : policyDir.listFiles()) {
            if (fileEntry.isFile()) {
                logger.atInfo().log("reading policy: " + fileEntry.getPath());
                try {
                    final byte[] encoded = Files.readAllBytes(fileEntry.toPath());
                    final String policyString = new String(encoded, StandardCharsets.UTF_8);
                    final AuthorizationPolicy policy = this.parser.parsePolicy(policyString);
                    this.policyMap.put(policy.getIdentifier(), policy);
                } catch (final ProviderException | IOException e) {
                    logger.atSevere().withCause(e).log("unable to parse policy: " + fileEntry.getPath());

                }
            }
        }
    }

    public void setCache(final boolean cache) {
        this.cache = cache;
    }


    public void setPath(final Path path) {
        this.path = path;
    }
}
