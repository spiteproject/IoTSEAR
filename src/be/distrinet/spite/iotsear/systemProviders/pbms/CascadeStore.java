package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.pbms.ContextStorage;
import be.distrinet.spite.iotsear.pbms.selector.ContextSelector;

import java.util.ArrayList;
import java.util.List;

public class CascadeStore extends ContextStorage {
    private final List<ContextStorage> stores;

    public CascadeStore(final List<ContextStorage> stores) {
        this.stores = stores;
    }

    @Override
    public void store(final ContextAttribute attribute) {
        for (final ContextStorage store : this.stores) {
            store.store(attribute);
        }
    }

    @Override
    public void prune(final ContextSelector selector) {
        for (final ContextStorage store : this.stores) {
            store.prune(selector);
        }
    }

    @Override
    public List<ContextAttribute> find(final ContextSelector selector) {
        final List<ContextAttribute> list = new ArrayList<>();
        for (final ContextStorage store : this.stores) {
            list.addAll(store.find(selector));
        }
        return list;
    }

    @Override
    public List<ContextAttribute> findBySubject(final String subjectID) {
        final List<ContextAttribute> list = new ArrayList<>();
        for (final ContextStorage store : this.stores) {
            list.addAll(store.findBySubject(subjectID));
        }
        return list;
    }

    @Override
    public List<ContextAttribute> findBySource(final String sourceID) {
        final List<ContextAttribute> list = new ArrayList<>();
        for (final ContextStorage store : this.stores) {
            list.addAll(store.findBySource(sourceID));
        }
        return list;
    }

    @Override
    public List<ContextAttribute> findBySourceType(final String sourceType) {
        final List<ContextAttribute> list = new ArrayList<>();
        for (final ContextStorage store : this.stores) {
            list.addAll(store.findBySourceType(sourceType));
        }
        return list;
    }

    @Override
    public String getProviderID() {
        return null;
    }

}
