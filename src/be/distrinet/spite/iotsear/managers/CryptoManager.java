package be.distrinet.spite.iotsear.managers;

import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.core.exceptions.InvalidConfigurationException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.crypto.HashSPI;
import be.distrinet.spite.iotsear.crypto.SignatureSPI;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pf4j.PluginManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CryptoManager {
    public static final String HASH_SHA265_SYSTEM_PROVIDER = "iotsear:crypto:hash:sha256";
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static CryptoManager ourInstance;
    private final PluginManager pluginManager;
    private Map<String, HashSPI> hashProviders;
    private Map<String, SignatureSPI> signatureProviders;

    private CryptoManager() {
        this.pluginManager = IoTSEAR.getInstance().getPluginManager();
    }

    public static CryptoManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new CryptoManager();
        }
        return ourInstance;
    }

    public void init(JSONArray json) throws InvalidConfigurationException {
        this.hashProviders = new HashMap<>();
        this.signatureProviders = new HashMap<>();
        for (final HashSPI provider : this.pluginManager.getExtensions(HashSPI.class)) {
            logger.atInfo().log("Loading Hash provider: %s", provider.getProviderID());
            this.hashProviders.put(provider.getProviderID(), provider);
        }
        for (final SignatureSPI provider : this.pluginManager.getExtensions(SignatureSPI.class)) {
            logger.atInfo().log("Loading Signature provider: %s", provider.getProviderID());
            this.signatureProviders.put(provider.getProviderID(), provider);
        }
        final Iterator it = json.iterator();
        while (it.hasNext()) {
            final JSONObject s = (JSONObject) it.next();
            initSignatureProvider(s);
        }
    }

    public HashSPI getHashProvider(final String providerID) throws ProviderNotFoundException {
        if (!this.hashProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.hashProviders.get(providerID);
    }

    public void initSignatureProvider(JSONObject json) throws InvalidConfigurationException {
        String providerID = json.get("provider").toString();
        SignatureSPI factory = signatureProviders.get(providerID);
        SignatureSPI newInstance = factory.configure(json);
        logger.atInfo().log("Configured new Signature provider: %s", newInstance.getIdentifier());
        signatureProviders.put(newInstance.getIdentifier(),newInstance);
    }

    public SignatureSPI getSignatureProvider(String providerID) throws ProviderNotFoundException {
        if (!this.signatureProviders.containsKey(providerID)) {
            throw new ProviderNotFoundException(providerID);
        }
        return this.signatureProviders.get(providerID);
    }
}
