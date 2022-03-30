package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.pbms.selector.ContextSelector;

import java.util.List;

public class EmptyContextStorage extends ContextStorage {
    @Override
    public void store(ContextAttribute attribute) {

    }

    @Override
    public void prune(ContextSelector selector) {

    }

    @Override
    public List<ContextAttribute> find(ContextSelector selector) {
        return null;
    }

    @Override
    public List<ContextAttribute> findBySubject(String subjectID) {
        return null;
    }

    @Override
    public List<ContextAttribute> findBySource(String sourceID) {
        return null;
    }

    @Override
    public List<ContextAttribute> findBySourceType(String sourceType) {
        return null;
    }

    @Override
    public String getProviderID() {
        return null;
    }
}
