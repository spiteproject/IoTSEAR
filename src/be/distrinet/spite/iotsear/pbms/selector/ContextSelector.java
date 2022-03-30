package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

public interface ContextSelector {
    boolean match(ContextAttribute attribute);
}
