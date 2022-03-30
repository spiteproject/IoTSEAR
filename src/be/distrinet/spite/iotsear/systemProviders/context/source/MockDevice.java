package be.distrinet.spite.iotsear.systemProviders.context.source;

import be.distrinet.spite.iotsear.core.model.context.ContextHandler;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

@Extension
public class MockDevice extends ContextSource {
    private String identifier;


    @Override
    public void enable() {
        super.handler.enable();
    }

    @Override
    public void retrieveContext(final ContextHandler.ContextProcessedListener listener) {
        handler.retrieveWithListener(listener);
    }


    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getProviderID() {
        return "sources:mock";
    }

    @Override
    public ContextSource createSource(final JSONObject json) {
        final MockDevice device = new MockDevice();
        device.identifier = json.get("identifier").toString();
        final JSONObject object = (JSONObject) json.get("metadata");
        for (final Object key : object.keySet()) {
            device.putMetaData(key.toString(), object.get(key).toString());
        }
        return device;
    }
}
