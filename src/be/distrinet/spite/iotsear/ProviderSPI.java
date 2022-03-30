package be.distrinet.spite.iotsear;

import org.pf4j.ExtensionPoint;

public interface ProviderSPI extends ExtensionPoint {
    String getProviderID();
}
