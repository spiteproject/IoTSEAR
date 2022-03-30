package be.distrinet.spite.iotsear.systemProviders.crypto;

import java.security.SecureRandom;

public class AES256 {
    public byte[] generateKey() {
        final byte[] key = new byte[256];
        new SecureRandom().nextBytes(key);
        return key;
    }

}
