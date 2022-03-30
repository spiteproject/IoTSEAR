package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;

public class SourceIDSelector implements ContextSelector {
    private final String sourceID;

    public SourceIDSelector(final ContextSource source) {
        this.sourceID = source.getIdentifier();
    }

    public SourceIDSelector(final String sourceID) {
        this.sourceID = sourceID;
    }

    @Override
    public boolean match(final ContextAttribute attribute) {
        return attribute.getSource().getIdentifier().equals(this.sourceID);
    }
}
