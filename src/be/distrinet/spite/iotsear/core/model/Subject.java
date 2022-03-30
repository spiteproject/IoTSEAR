package be.distrinet.spite.iotsear.core.model;

public class Subject {

    private final String id;

    public Subject(final String uuid) {
        this.id = uuid;
    }

    public String getIdentifier() {
        return this.id;
    }

}
