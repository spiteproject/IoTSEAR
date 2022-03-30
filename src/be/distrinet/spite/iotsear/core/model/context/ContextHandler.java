package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.IoTSEAR;
import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.pbms.ContextStorage;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


public abstract class ContextHandler implements ProviderSPI {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    protected ContextDecoder decoder;
    protected ContextStorage storage = IoTSEAR.getInstance().getContextStore();
    protected List<ContextProcessedListener> onProcessedListeners = new ArrayList<>();
    protected List<ContextPipe> pipes = new ArrayList<>();

    void setDecoder(final ContextDecoder decoder) {
        this.decoder = decoder;
    }

    protected void storeContext(final byte[] context) {
        final ContextAttribute attribute = this.decoder.decode(context);
        if (attribute != null) {
            this.storage.store(attribute);
            this.logger.atFine().log("storing object: " + attribute.getType());
        } else {
            this.logger.atSevere().log("unable to decode (and hence, store) context object");
        }
    }

    void setStorage(final ContextStorage storage) {
        this.storage = storage;
    }

    public abstract ContextHandler createContextHandler(JSONObject config);


    /**
     * Enables this context-handler
     */
    public abstract void enable();

    /**
     * adds a listener that will be called when this handler is done handling this context (i.e. after the context has been stored)
     *
     * @param listener
     */
    public void addOnContextProcessedListener(final ContextProcessedListener listener) {
        this.onProcessedListeners.add(listener);
    }

    /**
     * adds a pipe that can perform transformations to context objects, such as adding meta-data, and proofs
     * before the context is stored
     *
     * @param pipe
     */
    public void addOnContextPipe(final ContextPipe pipe) {
        this.pipes.add(pipe);
    }

    public abstract void retrieveWithListener(final ContextProcessedListener listener);

    /**
     * This method will process the context that has been received by this handler
     * The pipes are executed, after which the context is stored
     * next, the onContextProcessed listeners are notified
     * <p>
     * IMPORTANT: Handler implementations MUST call this method after they have received and processed a context attribute
     *
     * @param attribute
     */
    protected void handleContext(final ContextAttribute attribute) {
        ContextAttribute ctx = attribute;
        for (final ContextPipe pipe : this.pipes) {
            ContextAttribute tmp = null;
            try {
                tmp = pipe.processContext(ctx);
            } catch (final Exception e) {
                logger.atSevere().withCause(e).log("a context-pipe caused an exception");
            }

            if (tmp != null) {
                ctx = tmp;
            }
        }
        final ContextAttribute itsTheFinalContext = ctx;
        this.storage.store(itsTheFinalContext);
        this.onProcessedListeners.forEach(contextProcessedListener -> {
            try {
                contextProcessedListener.onContextProcessed(itsTheFinalContext);
            } catch (final Exception e) {
                logger.atSevere().withCause(e).log("An exception was thrown in this onContextProcessedListener ");
            }
        });
    }

    public interface ContextProcessedListener {
        void onContextProcessed(ContextAttribute attribute);
    }

    public interface ContextPipe {
        ContextAttribute processContext(ContextAttribute attribute);
    }

}
