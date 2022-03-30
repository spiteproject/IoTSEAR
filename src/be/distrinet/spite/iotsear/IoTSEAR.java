package be.distrinet.spite.iotsear;

import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import be.distrinet.spite.iotsear.managers.ContextManager;
import be.distrinet.spite.iotsear.managers.CryptoManager;
import be.distrinet.spite.iotsear.managers.PolicyFactoryManager;
import be.distrinet.spite.iotsear.managers.ProofManager;
import be.distrinet.spite.iotsear.pbms.*;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyTarget;
import com.google.common.flogger.FluentLogger;
import com.google.common.flogger.LoggerConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.RuntimeMode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class IoTSEAR {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private static IoTSEAR INSTANCE;
    private final PluginManager pluginManager;
    private ContextStorage contextStore;
    private PolicyRepository policyRepository;
    private PolicyEngine policyEnginge;


    private IoTSEAR() {
        this.pluginManager = new DefaultPluginManager() {
            @Override
            public RuntimeMode getRuntimeMode() {
                return RuntimeMode.DEVELOPMENT;
            }
        };
        this.pluginManager.loadPlugins();
        this.pluginManager.startPlugins();
    }

    public static void main(final String... strings) {
        final IoTSEAR ioTSEAR = IoTSEAR.getInstance();
        ioTSEAR.configure(new File("./IoTSEAR.json"));
        for (final AuthorizationPolicy policy : IoTSEAR.getInstance().policyRepository.retrievePolicies()) {
            System.out.println(policy.getIdentifier());
        }
        ioTSEAR.getPolicyEngine().enforce(new PolicyTarget(PolicyTarget.ANY_SUBJECT, PolicyTarget.ANY_RESOURCE, PolicyTarget.ANY_ACTION), new PolicyDecisionListener() {
            @Override
            public void processDecision(final AuthorizationPolicy.PolicyEffect effect, final PolicyTarget pepTarget) {
                System.out.println(effect);
            }
        });
    }

    public static synchronized IoTSEAR getInstance() {
        if (IoTSEAR.INSTANCE == null) {
            IoTSEAR.INSTANCE = new IoTSEAR();
        }
        return IoTSEAR.INSTANCE;
    }

    public void configure(final File jsonFile) {
        try {
            this.configure((JSONObject) new JSONParser().parse(new FileReader(jsonFile)));
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void configure(final JSONObject json) {
        this.pluginManager.stopPlugins();

        try {
            if(json.containsKey("pbms")) {
                final JSONObject pbms = (JSONObject) json.get("pbms");
                if(pbms.containsKey("repository")) {
                    final JSONObject repo = (JSONObject) pbms.get("repository");
                    final String repoProvider = repo.get("provider").toString();

                    for (final PolicyRepository provider : this.pluginManager.getExtensions(PolicyRepository.class)) {
                        if (provider.getProviderID().equals(repoProvider)) {
                            this.policyRepository = provider.init(repo);
                            logger.atInfo().log("Loading default repository: %s", provider.getProviderID());
                        }
                    }
                }else{
                    this.policyRepository = new DummyPolicyRepository();
                }
                if(pbms.containsKey("context-persistence")){
                final String storageString = ((JSONObject) pbms.get("context-persistence")).get("provider").toString();
                for (final ContextStorage provider : this.pluginManager.getExtensions(ContextStorage.class)) {
                    if (provider.getProviderID().equals(storageString)) {
                        this.contextStore = provider;
                        logger.atInfo().log("Loading default contextstore: %s", provider.getProviderID());
                    }
                }
                }else{
                    this.contextStore = new EmptyContextStorage();
                }
                if (pbms.containsKey("pdp")) {
                    JSONObject pdp = (JSONObject) pbms.get("pdp");
                    for (final PDP provider : this.pluginManager.getExtensions(PDP.class)) {
                        if (provider.getProviderID().equals(pdp.get("provider").toString())) {
                            this.policyEnginge = new PolicyEngine(provider, this.policyRepository, this.contextStore);
                            logger.atInfo().log("Loading default PDP: %s", provider.getProviderID());
                        }
                    }
                }else{
                    this.policyEnginge = new PolicyEngine(new DummyPDP(), this.policyRepository, this.contextStore);
                }
            }else{
                this.policyRepository = new DummyPolicyRepository();
                this.contextStore = new EmptyContextStorage();
                this.policyEnginge = new PolicyEngine(new DummyPDP(), this.policyRepository,this.contextStore);
            }
            ProofManager.getInstance().init();
            if(json.containsKey("proofs")) {
                final JSONArray proofs = (JSONArray) json.get("proofs");
                CryptoManager.getInstance().init(proofs);
            }
            if(json.containsKey("sources")) {
                final JSONArray sources = (JSONArray) json.get("sources");
                ContextManager.getInstance().init(sources);
            }



        } catch (final Exception e) {
            logger.atSevere().withCause(e).log("error processing IoTSEAR config");
        }


        this.pluginManager.startPlugins();

    }

    public ContextStorage getContextStore() {
        return this.contextStore;
    }

    public PolicyRepository getPolicyRepository() {
        return this.policyRepository;
    }

    public PolicyEngine getPolicyEngine() {
        return this.policyEnginge;
    }

    public ContextManager getContextManager() {
        return ContextManager.getInstance();
    }

    public PolicyFactoryManager getPolicyFactoryManager() {
        return PolicyFactoryManager.getInstance();
    }

    /**
     * gets the list of available context sources
     */
    public List<ContextSource> getContextSources() {
        return ContextManager.getInstance().getSources();
    }

    /**
     * internal framework method
     */
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }
}
