package be.distrinet.spite.iotsear.systemProviders.context.source;

import be.distrinet.spite.iotsear.core.model.context.ContextHandler;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import org.json.simple.JSONObject;


public abstract class DummySource extends ContextSource {
    @Override
    public void enable() {
    }

    @Override
    public void retrieveContext(ContextHandler.ContextProcessedListener listener) {
    }


    @Override
    public ContextSource createSource(JSONObject json) {
        return null;
    }
}
