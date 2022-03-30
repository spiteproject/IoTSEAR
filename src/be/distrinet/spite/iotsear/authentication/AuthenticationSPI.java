package be.distrinet.spite.iotsear.authentication;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.authentication.claim.Claim;
import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.credential.Credential;
import be.distrinet.spite.iotsear.core.model.credential.CredentialType;
import be.distrinet.spite.iotsear.core.model.credential.issuance.IssuanceConfiguration;

import java.util.List;

public interface AuthenticationSPI extends ProviderSPI {
    /**
     * Factory method for Issuance Configuration objects
     *
     * @param attributeList
     * @param type
     * @return an Issuance configuration object with a specific type related to this Authentication provider
     */
    public IssuanceConfiguration getIssuanceConfiguration(List<Attribute> attributeList, CredentialType type);

    public Credential generateCredential(IssuanceConfiguration configuration);

    public Credential generateCredential(IssuanceConfiguration configuration, Object issuerKey);

    public byte[] generateChallenge();

    public Proof createProof(Claim claim, List<Credential> Credentials);
}
