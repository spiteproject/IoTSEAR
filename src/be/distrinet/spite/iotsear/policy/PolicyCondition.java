package be.distrinet.spite.iotsear.policy;


import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.pbms.ContextStorage;

import java.util.ArrayList;
import java.util.List;

public class PolicyCondition implements PolicyConditionSet {
    protected String source;
    protected PolicyConditionOperation operation;
    protected String value;
    protected List<PolicyConditionVerifier> verifiers;


    public String getSource() {
        return this.source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public PolicyConditionOperation getOperation() {
        return this.operation;
    }

    public void setOperation(final PolicyConditionOperation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public List<PolicyConditionVerifier> getVerifiers() {
        return this.verifiers;
    }

    public void setVerifiers(final List<PolicyConditionVerifier> verifiers) {
        if (this.verifiers == null) {
            this.verifiers = verifiers;
        }
    }

    @Override
    public String toString() {
        return "PolicyCondition{" +
                "source='" + this.source + '\'' +
                ", operation=" + this.operation +
                ", value='" + this.value + '\'' +
                ", verifiers=" + this.verifiers +
                '}';
    }

    @Override
    public PolicyConditionEvaluationResult evaluate(final ContextStorage storage, final AuthorizationPolicy policy) {
        final List<ContextAttribute> attributes = storage.findBySourceOrType(this.source);
        final List<ContextAttribute> filtered = new ArrayList<>(attributes.size());
        String theValue = this.value;
        if(theValue.equals("darc:subject")) theValue = policy.getPepTarget().getSubject();
        else if(theValue.equals("darc:resource")) theValue = policy.getPepTarget().getResource();
        else if(theValue.equals("darc:action")) theValue = policy.getPepTarget().getAction();
        String theFinalValue = theValue;
        attributes.forEach(attribute -> {
            boolean ok = true;
            if (this.operation.match(this.source, theFinalValue, attribute, policy)) {
                for (final PolicyConditionVerifier verifier : this.verifiers) {
                    ok = verifier.verify(attribute, policy) && ok;
                }
                if (ok) {
                    filtered.add(attribute);
                }
            }
        });

        return new PolicyConditionEvaluationResult(filtered);
    }


}
