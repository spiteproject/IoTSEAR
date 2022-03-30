package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.core.model.Subject;

import java.util.List;

public class Environment extends Subject {
    private final List<ContextSource> environmentDevices;

    public Environment(final String identifier, final List<ContextSource> environmentDevices) {
        super(identifier);
        this.environmentDevices = environmentDevices;
    }
    public List<ContextSource> getEnvironmentDevices() {
        return this.environmentDevices;
    }
}
