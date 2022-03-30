package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.core.exceptions.ProviderException;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;

public interface PolicyParser {
    /**
     * Parses a String to an AuthorizationPolicy object
     * @param policy
     * @return
     * @throws ProviderException
     */
    AuthorizationPolicy parsePolicy(String policy) throws ProviderException;
}
