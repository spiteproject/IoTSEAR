package be.distrinet.spite.iotsear.pbms;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.pbms.selector.ContextSelector;

import java.util.ArrayList;
import java.util.List;

public abstract class ContextStorage implements ProviderSPI {
    /**
     * Store a context attribute in this storage
     *
     * @param attribute the context attribute
     */
    public abstract void store(ContextAttribute attribute);

    /**
     * Delete the attributes that match with the given selector from this storage
     *
     * @param selector
     */
    public abstract void prune(ContextSelector selector);

    /**
     * Find the list of contextattributes given the context selector
     */
    public abstract List<ContextAttribute> find(ContextSelector selector);

    /**
     * Find the list of contextattributes given the subject-identifier
     */
    public abstract List<ContextAttribute> findBySubject(String subjectID);

    /**
     * Find the list of contextattributes given the source-identifier
     */
    public abstract List<ContextAttribute> findBySource(String sourceID);

    /**
     * Find the list of contextattributes given the source-type
     */
    public abstract List<ContextAttribute> findBySourceType(String sourceType);

    public List<ContextAttribute> findBySourceOrType(final String sourceOrType) {
        List<ContextAttribute> ret = findBySourceType(sourceOrType);
        if (ret.isEmpty()) {
            ret = findBySource(sourceOrType);
            if(ret.isEmpty()){
                ret = find((ContextAttribute attribute)->attribute.getType().equals(sourceOrType));
            }
            return ret;
        } else {
            return ret;
        }

    }


}
