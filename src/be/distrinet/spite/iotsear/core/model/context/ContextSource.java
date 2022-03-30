package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.ProviderSPI;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class ContextSource implements ProviderSPI {
    protected final Map<String, String> sourceMetaData = new HashMap<>();
    protected ContextHandler handler;

    /**
     * attempts to retrieve source output.
     * this method is asynchronous. The configured listeners will be notified when the source responds.
     */
    public abstract void enable();

    /**
     * attempts to retrieve source output.
     * this method is asynchronous. ONLY the provided listener will be notified when the source responds.
     */
    public abstract void retrieveContext(ContextHandler.ContextProcessedListener listener);

    public void putMetaData(final String type, final String value) {
        this.sourceMetaData.put(type, value);
    }

    public String getMetaData(final String type) {
        return this.sourceMetaData.get(type);
    }

    public Map<String, String> getMetaData() {
        return new HashMap<>(this.sourceMetaData);
    }

    public abstract String getIdentifier();

    public String getSourceType() {
        return getMetaData("contextType");
    }

    public void setSourceType(final String type) {
        putMetaData("contextType", type);
    }


    @Override
    public abstract String getProviderID();

    /**
     * factory method to create a new Context Source object from the IoTSEAR configuration
     *
     * @param json
     * @return
     */
    public abstract ContextSource createSource(JSONObject json);

    public void setContextHandler(final ContextHandler handler) {
        this.handler = handler;
        this.handler.addOnContextPipe(attribute -> {
            attribute.setSource(this);
            return attribute;
        });
    }

    /**
     * add a listener that will be called when context from this device is received.
     *
     * @param listener
     */
    public void addOnContextProcessedListener(final ContextHandler.ContextProcessedListener listener) {
        if (this.handler != null) {
            this.handler.addOnContextProcessedListener(listener);
        }
    }

    /**
     * adds a pipe that can perform transformations to context objects, such as adding meta-data, and proofs
     * before the context is stored
     *
     * @param pipe
     */
    public void addContextPipe(final ContextHandler.ContextPipe pipe) {
        if (this.handler != null) {
            this.handler.addOnContextPipe(pipe);
        }
    }


}
