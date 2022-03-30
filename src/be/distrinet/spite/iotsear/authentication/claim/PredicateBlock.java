package be.distrinet.spite.iotsear.authentication.claim;

public class PredicateBlock {
    private final CredentialBlock parent;
    private final String attributeIdentifier;
    private final Operator operator;
    private final String value;

    public PredicateBlock(final CredentialBlock parent, final String attributeIdentifier, final Operator operator, final String value) {
        this.parent = parent;
        this.attributeIdentifier = attributeIdentifier;
        this.operator = operator;
        this.value = value;
    }

    public CredentialBlock getParent() {
        return this.parent;
    }

    public String getAttributeIdentifier() {
        return this.attributeIdentifier;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }

    public enum Operator {
        LT, LEQ, EQ, GEQ, GT
    }
}
