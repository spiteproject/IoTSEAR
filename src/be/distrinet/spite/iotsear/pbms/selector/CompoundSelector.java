package be.distrinet.spite.iotsear.pbms.selector;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;

import java.util.List;

public class CompoundSelector implements ContextSelector {
    private final List<ContextSelector> selectors;
    private int threshold = 1;

    public CompoundSelector(final List<ContextSelector> selectors, final int threshold) {
        this.selectors = selectors;
        this.threshold = threshold;
    }

    public CompoundSelector(final List<ContextSelector> selectors) {
        this.selectors = selectors;
        this.threshold = this.selectors.size();
    }


    @Override
    public boolean match(final ContextAttribute attribute) {
        int succeeded = 0;
        int left = this.selectors.size();
        while (succeeded != this.threshold || this.threshold - succeeded < left) {
            left--;
            if (this.selectors.get(left).match(attribute)) {
                succeeded++;
            }
        }
        return succeeded == this.threshold;
    }
}
