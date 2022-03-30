package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import be.distrinet.spite.iotsear.core.model.context.Environment;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentSelector implements ContextSelector {
    private final List<String> deviceIdentifiers;

    public EnvironmentSelector(final Environment environment) {
        this.deviceIdentifiers = new ArrayList<>();
        for (final ContextSource source : environment.getEnvironmentDevices()) {
            this.deviceIdentifiers.add(source.getIdentifier());
        }
    }

    @Override
    public boolean match(final ContextAttribute attribute) {
        return this.deviceIdentifiers.contains(attribute.getSource().getIdentifier());
    }
}
