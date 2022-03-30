package be.distrinet.spite.iotsear.core.model.credential.issuance;

public abstract class Issuer {

    public abstract boolean equals(Issuer otherIssuer);

    public abstract String getProviderID();
}
