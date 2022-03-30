package be.distrinet.spite.iotsear.managers;

import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.context.*;
import be.distrinet.spite.iotsear.core.model.context.proof.AuthenticityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.IntegrityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.OwnershipProof;
import be.distrinet.spite.iotsear.pbms.ContextStorage;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pf4j.PluginManager;

import java.util.*;

public class ContextManager {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static ContextManager ourInstance;
    private final PluginManager pluginManager;
    private Map<String, ContextEncoder> encoders;
    private Map<String, ContextDecoder> decoders;
    private Map<String, ContextSource> sources;

    private Map<String, ContextHandler> handlers;
    private Map<String, ContextStorage> storageMap;


    private ContextManager() {
        this.pluginManager = IoTSEAR.getInstance().getPluginManager();
    }

    public static ContextManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ContextManager();
        }
        return ourInstance;
    }

    public void init(final JSONArray config) {
        this.encoders = new HashMap<>();
        for (final ContextEncoder provider : this.pluginManager.getExtensions(ContextEncoder.class)) {
            logger.atInfo().log("Loading encoder: %s", provider.getProviderID());
            this.encoders.put(provider.getProviderID(), provider);
        }
        this.decoders = new HashMap<>();
        for (final ContextDecoder provider : this.pluginManager.getExtensions(ContextDecoder.class)) {
            logger.atInfo().log("Loading decoder: %s", provider.getProviderID());
            this.decoders.put(provider.getProviderID(), provider);
        }

        this.sources = new HashMap<>();
        final HashMap<String, ContextSource> sourceTypes = new HashMap<>();
        for (final ContextSource provider : this.pluginManager.getExtensions(ContextSource.class)) {
            logger.atInfo().log("Loading Source provider: %s", provider.getProviderID());
            sourceTypes.put(provider.getProviderID(), provider);
        }


        this.storageMap = new HashMap<>();
        for (final ContextStorage provider : this.pluginManager.getExtensions(ContextStorage.class)) {
            logger.atInfo().log("Loading storage provider: %s", provider.getProviderID());
            this.storageMap.put(provider.getProviderID(), provider);
        }
        this.handlers = new HashMap<>();
        for (final ContextHandler provider : this.pluginManager.getExtensions(ContextHandler.class)) {
            logger.atInfo().log("Loading context handler: %s", provider.getProviderID());
            this.handlers.put(provider.getProviderID(), provider);
        }

        final Iterator it = config.iterator();
        while (it.hasNext()) {
            final JSONObject s = (JSONObject) it.next();
            final String provider = s.get("provider").toString();
            final ContextSource prototype = sourceTypes.get(provider);
            if (prototype == null) {
                logger.atSevere().withCause(new ProviderNotFoundException(provider)).log("cannot load provider");
            } else {
                final ContextSource theSource = prototype.createSource(s);
                final JSONObject contextHandler = (JSONObject) s.get("contextHandler");
                final ContextHandler theContextHandler = getContextHandler(contextHandler.get("provider").toString()).createContextHandler(contextHandler);
                theSource.setContextHandler(theContextHandler);
                theSource.addContextPipe(attribute -> {
                    if(!attribute.getMetaData().containsKey(ContextAttribute.TS))
                        attribute.setTimestamp(System.currentTimeMillis());
                    return attribute;
                });
                theSource.addContextPipe(attribute -> {
                    attribute.setSource(theSource);
                    return attribute;
                });
                if(theSource.getMetaData().containsKey("authenticity")){
                    theSource.addContextPipe(attribute -> {
                        attribute.putMetaData(ContextAttribute.APP, theSource.getMetaData("authenticity"));
                        if(theSource.getMetaData().containsKey("attach-proof")){
                            final String app = theSource.getMetaData("attach-proof");
                            try{
                                AuthenticityProof proof = ProofManager.getInstance().createAuthenticityProof(app,theSource,attribute);
                                attribute.setAuthenticityProof(proof);
                            }catch (Exception e){
                                logger.atWarning().withCause(e).log();
                            }
                        }
                        return attribute;
                    });
                }
                if(theSource.getMetaData().containsKey("integrity")){
                    theSource.addContextPipe(attribute -> {
                        attribute.putMetaData(ContextAttribute.IPP, theSource.getMetaData("integrity"));
                        if(theSource.getMetaData().containsKey("attach-proof")){
                            final String ipp = theSource.getMetaData("attach-proof");
                            try{
                                IntegrityProof proof = ProofManager.getInstance().createIntegrityProof(ipp,attribute);
                                attribute.setIntegrityProof(proof);
                            }catch (Exception e){
                                logger.atWarning().withCause(e).log();
                            }
                        }
                        return attribute;
                    });
                }
                if(theSource.getMetaData().containsKey("ownership")){
                    theSource.addContextPipe(attribute -> {
                        attribute.putMetaData(ContextAttribute.OPP, theSource.getMetaData("ownership"));
                        if(theSource.getMetaData().containsKey("attach-proof")){
                            final String opp = theSource.getMetaData("attach-proof");
                            try{
                                OwnershipProof proof = ProofManager.getInstance().createOwnershipProof(opp,attribute);
                                attribute.setOwnershipProof(proof);
                            }catch (Exception e){
                                logger.atWarning().withCause(e).log();
                            }
                        }
                        return attribute;
                    });
                }

                theSource.enable();
                this.sources.put(theSource.getIdentifier(), theSource);
                logger.atInfo().log("loaded device: " + theSource.getIdentifier());
            }
        }
    }

    /**
     * Get the ContextSource object associated with the given identifier.
     *
     * @param identifier
     * @return the ContextSource object who's ID is equal to the given identifier or if none are available with the given ID
     */
    public ContextSource getSource(final String identifier) {
        return this.sources.get(identifier);
    }

    /**
     * gets the list of available context sources
     */
    public List<ContextSource> getSources() {
        return new ArrayList<>(this.sources.values());
    }

    /**
     * Get a list of ContextSource objects associated with the given source type.
     *
     * @param type
     * @return the list of ContextSource objects who's type is equal to the given type
     * @throws ProviderNotFoundException
     */
    public List<ContextSource> getSourcesByType(final String type) {
        final List<ContextSource> sourceList = new ArrayList<>();
        for (final ContextSource source : this.sources.values()) {
            if (type.equals(source.getMetaData("contextType"))) {
                sourceList.add(source);
            }
        }
        return sourceList;
    }

    /**
     * get the encoder associated with the given provider ID
     * <p>
     * SystemProviders are:
     * iotsear:context:encoders:string-encoder-decoder
     * iotsear:context:encoders:protobuf-encoder-decoder
     *
     * @param providerID
     * @return
     */
    public ContextEncoder getEncoder(final String providerID) {
        return this.encoders.get(providerID);
    }

    /**
     * get the decoder associated with the given provider ID
     * <p>
     * SystemProvider IDs are:
     * iotsear:context:encoders:string-encoder-decoder
     * iotsear:context:encoders:protobuf-encoder-decoder
     *
     * @param providerID
     * @return
     */
    public ContextDecoder getDecoder(final String providerID) {
        return this.decoders.get(providerID);
    }

    /**
     * Get a ContextStore associated with the given provider ID
     * <p>
     * SystemProvider IDs are:
     * "iotsear:context:store:memory"
     *
     * @param providerID
     * @return
     */
    public ContextStorage getContextStore(final String providerID) {
        return this.storageMap.get(providerID);
    }

    /**
     * Get a ContextHandler associated with the given provider ID
     * <p>
     * SystemProvider IDs are:
     * "iotsear:context:handler:mqttReceiver"
     * "iotsear:context:handler:mock"
     *
     * @param providerID
     * @return
     */
    public ContextHandler getContextHandler(final String providerID) {
        return this.handlers.get(providerID);
    }


}
