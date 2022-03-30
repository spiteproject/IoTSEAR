package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.ProviderSPI;

public interface ContextDecoder extends ProviderSPI {
    /**
     * Decodes a byte-array into a ContextAttribute
     *
     * @param bytes the byte-array
     * @return a ContextAttribute object, or null if the bytes could not successfully be decoded.
     */
    ContextAttribute decode(byte[] bytes);
}
