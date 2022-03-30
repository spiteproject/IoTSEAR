package be.distrinet.spite.iotsear.managers;

import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import be.distrinet.spite.iotsear.core.model.context.proof.AuthenticityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.ContextProof;
import be.distrinet.spite.iotsear.core.model.context.proof.IntegrityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.OwnershipProof;
import be.distrinet.spite.iotsear.crypto.SignatureSPI;
import com.google.common.flogger.FluentLogger;
import org.pf4j.PluginManager;

import java.util.HashMap;
import java.util.Map;

public class ProofManager {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static ProofManager ourInstance;
    private final PluginManager pluginManager;
    private Map<String, IntegrityProof> ipProviders;
    private Map<String, AuthenticityProof> apProviders;
    private Map<String, OwnershipProof> opProviders;

    private ProofManager() {
        this.pluginManager = IoTSEAR.getInstance().getPluginManager();
    }

    public static ProofManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ProofManager();
        }
        return ourInstance;
    }

    public void init() {
        this.ipProviders = new HashMap<>();
        this.apProviders = new HashMap<>();
        this.opProviders = new HashMap<>();
        for (final IntegrityProof provider : this.pluginManager.getExtensions(IntegrityProof.class)) {
            logger.atInfo().log("Loading IntegrityProof provider: %s", provider.getProviderID());
            this.ipProviders.put(provider.getProviderID(), provider);
        }
        for (final AuthenticityProof provider : this.pluginManager.getExtensions(AuthenticityProof.class)) {
            logger.atInfo().log("Loading AuthenticityProof provider: %s", provider.getProviderID());
            this.apProviders.put(provider.getProviderID(), provider);
        }
        for (final OwnershipProof provider : this.pluginManager.getExtensions(OwnershipProof.class)) {
            logger.atInfo().log("Loading OwnershipProof provider: %s", provider.getProviderID());
            this.opProviders.put(provider.getProviderID(), provider);
        }
    }




    public AuthenticityProof getAuthenticityProof(final String providerID,String proofString, ContextAttribute attribute) throws ProviderNotFoundException {
        if (!this.apProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.apProviders.get(providerID).getAuthenticityProof(proofString,attribute);
    }

    public IntegrityProof getIntegrityProof(final String providerID, String proofString, ContextAttribute attribute) throws ProviderNotFoundException {
        if(!this.ipProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.ipProviders.get(providerID).getIntegrityProof(proofString, attribute);
    }


    public OwnershipProof getOwnershipProof(final String providerID, String proofString, ContextAttribute attribute) throws ProviderNotFoundException {
        if(!this.opProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.opProviders.get(providerID).getOwnershipProof(proofString, attribute);
    }

    public AuthenticityProof createAuthenticityProof(final String providerID, ContextSource source, ContextAttribute attribute) throws ProviderNotFoundException, InvalidSignatureException {
        if(!this.apProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.apProviders.get(providerID).createAuthenticityProof(source,attribute);
    }

    public IntegrityProof createIntegrityProof(final String providerID, ContextAttribute attribute) throws ProviderNotFoundException {
        if(!this.ipProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.ipProviders.get(providerID).createIntegrityProof(attribute);
    }


    public OwnershipProof createOwnershipProof(final String providerID, ContextAttribute attribute) throws ProviderNotFoundException {
        if(!this.opProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.opProviders.get(providerID).createOwnershipProof(attribute);
    }
}
