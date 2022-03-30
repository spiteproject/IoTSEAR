package be.distrinet.spite.iotsear.systemProviders.context.handler;


import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.core.exceptions.NotImplementedException;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextDecoder;
import be.distrinet.spite.iotsear.core.model.context.ContextHandler;
import be.distrinet.spite.iotsear.managers.ContextManager;
import com.google.common.flogger.FluentLogger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

import java.util.Random;

@Extension
public class MqttContextHandler extends ContextHandler implements IMqttMessageListener {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private String broker;
    private String topic;
    private MqttConnectOptions connOpts;
    private String clientId;
    private Thread runningThread;
    private ContextDecoder decoder;

    @Override
    public ContextHandler createContextHandler(final JSONObject config) {
        final MqttContextHandler retriever = new MqttContextHandler();
        retriever.broker = config.get("broker").toString();
        retriever.topic = config.get("topic").toString();
        retriever.connOpts = new MqttConnectOptions();
        retriever.connOpts.setCleanSession(true);
        retriever.connOpts.setAutomaticReconnect(true);
        retriever.clientId = new Random().nextLong() + "";
        retriever.decoder = ContextManager.getInstance().getDecoder(config.get("decoder").toString());
        retriever.storage = IoTSEAR.getInstance().getContextStore();

        return retriever;
    }

    @Override
    public void enable() {
        if (this.runningThread == null) {
            this.runningThread = new Thread(() -> {
                try {
                    final MqttClient mqttClient;
                    mqttClient = new MqttClient(this.broker, this.clientId, new MemoryPersistence());
                    mqttClient.connect(this.connOpts);
                    mqttClient.subscribe(this.topic, this);
                    logger.atInfo().log("Subscribing to topic: " + this.topic);
                    while (true) {
                        Thread.sleep(200);
                    }
                } catch (final MqttException | InterruptedException e) {
                    logger.atSevere().withCause(e).log("mqtt exception");
                }
            });
        }
        if (this.runningThread.isAlive()) {
            return;
        } else {
            this.runningThread.start();
        }

    }

    @Override
    public void retrieveWithListener(final ContextProcessedListener listener) {
        throw new NotImplementedException();
    }


    @Override
    public String getProviderID() {
        return "iotsear:context:handler:mqttReceiver";
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage message) {
        new Thread(() -> {
            try {
                final ContextAttribute attribute = this.decoder.decode(message.getPayload());
                if (attribute != null) {
                    super.handleContext(attribute);
                }
            } catch (final Exception e) {
                logger.atSevere().withCause(e).log("An exception was thrown while message arrived");
            }

        }).start();
    }
}