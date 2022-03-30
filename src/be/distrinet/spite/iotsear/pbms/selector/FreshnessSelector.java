package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public class FreshnessSelector implements ContextSelector {
    private final long freshness;

    public FreshnessSelector(final long freshness) {
        this.freshness = freshness;
    }

    @Override
    public boolean match(final ContextAttribute attribute) {
        return attribute.getTimestamp() - System.currentTimeMillis() < this.freshness;
    }
}
