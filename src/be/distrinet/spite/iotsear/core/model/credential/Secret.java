package be.distrinet.spite.iotsear.core.model.credential;

import be.distrinet.spite.iotsear.core.model.Attribute;

public abstract class Secret implements Attribute {

    private static final String SECRET_NAME = "___SECRET___";

    @Override
    public String getType() {
        return Secret.SECRET_NAME;
    }
}
