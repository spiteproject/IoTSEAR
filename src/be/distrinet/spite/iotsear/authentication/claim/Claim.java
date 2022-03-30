package be.distrinet.spite.iotsear.authentication.claim;

import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.credential.Credential;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Claim {
    private final Map<CredentialBlock, Credential> credentialMap;
    private final List<PredicateBlock> predicates;
    private String providerID = "";

    public Claim(final List<CredentialBlock> credentialBlocks, final List<PredicateBlock> predicates) {
        this.credentialMap = new HashMap<>();
        for (final CredentialBlock block : credentialBlocks) {
            this.credentialMap.put(block, null);
            if (!this.getProviderID().isEmpty() && !block.getProviderID().equals(this.providerID)) {
                throw new IllegalArgumentException("All credential blocks must have the same providerID");
            } else if (this.providerID.isEmpty()) {
                this.providerID = block.getProviderID();
            }
        }
        this.predicates = predicates;
    }

    public void clearCredentials() {
        final List<CredentialBlock> credentialBlocks = new ArrayList<>(this.credentialMap.keySet());
        this.credentialMap.clear();
        for (final CredentialBlock block : credentialBlocks) {
            this.credentialMap.put(block, null);
        }
    }

    /**
     * initializes this claim with matching credentials so it can be used to create a proof
     *
     * @param credentials
     * @return true if the operation succeeded and a proof can be constructed, false otherwise
     */
    public boolean initializeClaim(final List<Credential> credentials) {
        for (final Credential cred : credentials) {
            this.setCredential(cred);
        }
        for (final Credential cred : this.credentialMap.values()) {
            if (cred == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tries to set a credential for this claim.
     *
     * @param credential which will be used to prove (part of) this claim
     * @return true if the operation succeeded, false otherwise (if this credential cannot be used to prove the claim)
     */
    public boolean setCredential(final Credential credential) {
        final CredentialBlock theBlock = this.matchCredential(credential);
        if (theBlock == null) {
            return false;
        }
        this.credentialMap.put(theBlock, credential);
        return true;
    }

    /**
     * check whether or not a given credential can be used to prove (part of) this claim
     *
     * @param credential the credential to be tested
     * @return the credentialBlock that matches with the given credential, or null if no
     * suitable block was found
     */
    public CredentialBlock matchCredential(final Credential credential) {
        for (final CredentialBlock block : this.credentialMap.keySet()) {
            if (block.getCredentialType().matches(credential)) {
                if (this.checkPredicates(block, credential)) {
                    return block;
                }
            }
        }
        return null;
    }

    private boolean checkPredicates(final CredentialBlock block, final Credential credential) {
        for (final PredicateBlock predicateBlock : this.predicates) {
            if (predicateBlock.getParent().equals(block)) {
                boolean satisfied = false;
                final Attribute attribute = credential.getAttribute(predicateBlock.getAttributeIdentifier());
                switch (predicateBlock.getOperator()) {
                    case LT:
                        satisfied = Long.parseLong(attribute.getValue()) < Long.parseLong(predicateBlock.getValue());
                        break;
                    case LEQ:
                        satisfied = Long.parseLong(attribute.getValue()) <= Long.parseLong(predicateBlock.getValue());
                        break;
                    case EQ:
                        satisfied = attribute.getValue().equals(predicateBlock.getValue());
                        break;
                    case GEQ:
                        satisfied = Long.parseLong(attribute.getValue()) >= Long.parseLong(predicateBlock.getValue());
                        break;
                    case GT:
                        satisfied = Long.parseLong(attribute.getValue()) > Long.parseLong(predicateBlock.getValue());
                        break;
                }
                if (!satisfied) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<CredentialBlock, Credential> getCredentialMap() {
        return this.credentialMap;
    }

    public List<PredicateBlock> getPredicates() {
        return this.predicates;
    }


    public String getProviderID() {
        return this.providerID;
    }
}
