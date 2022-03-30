package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public class StalenessSelector implements ContextSelector {
    private final long maxAge;

    public StalenessSelector(final long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean match(final ContextAttribute attribute) {
        return Long.parseLong(attribute.getMetaData("timestamp")) - System.currentTimeMillis() > this.maxAge;
    }
}
