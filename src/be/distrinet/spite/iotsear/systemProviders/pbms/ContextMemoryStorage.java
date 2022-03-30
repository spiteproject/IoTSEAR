package be.distrinet.spite.iotsear.systemProviders.pbms;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextSource;
import be.distrinet.spite.iotsear.pbms.ContextStorage;
import be.distrinet.spite.iotsear.pbms.selector.ContextSelector;
import com.google.common.flogger.FluentLogger;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class ContextMemoryStorage extends ContextStorage {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final HashMap<Long, ContextAttribute> contextMap;
    private final HashMap<String, List<ContextAttribute>> typeLookup;
    private final HashMap<String, List<ContextAttribute>> sourceLookup;
    private final HashMap<String, List<ContextAttribute>> subjectLookup;
    private long index = 0;


    public ContextMemoryStorage() {
        this.contextMap = new HashMap<>();
        this.typeLookup = new HashMap<>();
        this.sourceLookup = new HashMap<>();
        this.subjectLookup = new HashMap<>();
    }

    @Override
    public void store(final ContextAttribute attribute) {
        synchronized (this.contextMap) {
            this.contextMap.put(this.index, attribute);
            if (attribute.getSubject() != null) {
                addToMap(attribute.getSubject().getIdentifier(), attribute, this.subjectLookup);
            }
            final ContextSource source = attribute.getSource();
            if (source != null) {
                addToMap(source.getIdentifier(), attribute, this.sourceLookup);

                final String type = source.getSourceType();
                if (type != null) {
                    addToMap(type, attribute, this.typeLookup);
                }
            }
            this.index++;
        }
        //logger.atInfo().log("stored attribute: " + attribute.getType());
    }

    //    private void addToMap(final String key, final int value, final Map<String, List<Integer>> map) {
//        synchronized (this.contextMap) {
//            if (map.containsKey(key)) {
//                map.get(key).add(value);
//            } else {
//                final List<Integer> theList = new ArrayList<>();
//                theList.add(value);
//                map.put(key, theList);
//            }
//        }
//    }
    private void addToMap(final String key, final ContextAttribute value, final Map<String, List<ContextAttribute>> map) {
        synchronized (this.contextMap) {
            if (map.containsKey(key)) {
                map.get(key).add(value);
            } else {
                final List<ContextAttribute> theList = new ArrayList<>();
                theList.add(value);
                map.put(key, theList);
            }
        }
    }

    private void removeFromMap(final String key, final int value, final Map<String, List<Integer>> map) {
        synchronized (this.contextMap) {
            if (map.containsKey(key)) {
                map.get(key).remove(value);
            }
        }
    }

    @Override
    public void prune(final ContextSelector selector) {
        synchronized (this.contextMap) {

        }
    }

    @Override
    public List<ContextAttribute> find(final ContextSelector selector) {
        final List<ContextAttribute> attributes;
        synchronized (this.contextMap) {
            attributes = new ArrayList<>(this.contextMap.values());
        }
        final List<ContextAttribute> theList = new ArrayList<>();
        for (final ContextAttribute attribute : attributes) {
            if (selector.match(attribute)) {
                theList.add(attribute);
            }
        }
        return theList;
    }

    @Override
    public List<ContextAttribute> findBySubject(final String subjectID) {
//        final List<Integer> subjects;
//        synchronized (this.contextMap) {
//            subjects = new ArrayList<>(this.subjectLookup.get(subjectID));
//        }
//        final List<ContextAttribute> theList = new ArrayList<>();
//        for (final Integer index : subjects) {
//            theList.add(this.contextMap.get(index));
//        }
//        return theList;

        return (this.subjectLookup.containsKey(subjectID) ? new ArrayList<>(this.subjectLookup.get(subjectID)) : new ArrayList<>());

    }

    @Override
    public List<ContextAttribute> findBySource(final String sourceID) {
//        final List<Integer> sources;
//        if (!this.sourceLookup.containsKey(SourceID)) {
//            return new ArrayList<>();
//        }
//        synchronized (this.contextMap) {
//            sources = new ArrayList<>(this.sourceLookup.get(SourceID));
//        }
//        final List<ContextAttribute> theList = new ArrayList<>();
//        for (final Integer index : sources) {
//            theList.add(this.contextMap.get(index));
//        }
//        return theList;

        return (this.sourceLookup.containsKey(sourceID) ? new ArrayList<>(this.sourceLookup.get(sourceID)) : new ArrayList<>());

    }

    @Override
    public List<ContextAttribute> findBySourceType(final String sourceType) {
//        final List<Integer> types;
//        if (!this.typeLookup.containsKey(sourceType)) {
//            return new ArrayList<>();
//        }
//        synchronized (this.contextMap) {
//            types = new ArrayList<>(this.typeLookup.get(sourceType));
//        }
//        final List<ContextAttribute> theList = new ArrayList<>();
//        for (final Integer index : types) {
//            theList.add(this.contextMap.get(index));
//        }
//        return theList;

        return (this.typeLookup.containsKey(sourceType) ? new ArrayList<>(this.typeLookup.get(sourceType)) : new ArrayList<>());

    }

    @Override
    public String getProviderID() {
        return "iotsear:context:store:memory";
    }
}
