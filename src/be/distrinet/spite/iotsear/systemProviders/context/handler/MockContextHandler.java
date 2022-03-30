package be.distrinet.spite.iotsear.systemProviders.context.handler;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextHandler;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

@Extension
public class MockContextHandler extends ContextHandler {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private int pollingInterval = 5000;
    private Thread runningThread;
    private int i = 0;

    @Override
    public ContextHandler createContextHandler(JSONObject config) {
        MockContextHandler handler = new MockContextHandler();
        if(config.containsKey("polling-interval")){
            handler.pollingInterval = Integer.parseInt(config.get("polling-interval").toString());
        }
        return handler;
    }
    @Override
    public void enable() {
        if (this.runningThread == null) {
            this.runningThread = new Thread(() -> {
                while (true){
                    super.handleContext(generateMockContext());
                    i++;
                    try {
                        Thread.sleep(pollingInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            this.runningThread.start();
        }
    }
    private synchronized ContextAttribute generateMockContext(){
        ContextAttribute attribute = new ContextAttribute("mock",i+"");
        i++;
        return attribute;
    }
    @Override
    public void retrieveWithListener(ContextProcessedListener listener) {
        ContextAttribute attribute = generateMockContext();
        super.handleContext(attribute);
        listener.onContextProcessed(attribute);
    }

    @Override
    public String getProviderID() {
        return "iotsear:context:handler:mock";
    }
}
