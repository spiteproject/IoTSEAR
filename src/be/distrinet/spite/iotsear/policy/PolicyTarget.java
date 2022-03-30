package be.distrinet.spite.iotsear.policy;

public class PolicyTarget {
    public final static String ANY_SUBJECT = "anySubject";
    public final static String ANY_RESOURCE = "anyResource";
    public final static String ANY_ACTION = "anyAction";
    private final String subject;
    private final String resource;
    private final String action;

    public PolicyTarget(final String subject, final String resource, final String action) {
        this.subject = subject;
        this.resource = resource;
        this.action = action;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getResource() {
        return this.resource;
    }

    public String getAction() {
        return this.action;
    }
}
