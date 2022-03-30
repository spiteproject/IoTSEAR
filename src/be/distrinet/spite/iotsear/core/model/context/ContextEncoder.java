package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.ProviderSPI;

public interface ContextEncoder extends ProviderSPI {
    /**
     * encode a ContextAttribute to a byte-array
     *
     * @param attribute
     * @return
     */
    byte[] encode(ContextAttribute attribute);
}
