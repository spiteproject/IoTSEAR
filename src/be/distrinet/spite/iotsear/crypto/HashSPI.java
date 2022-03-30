package be.distrinet.spite.iotsear.crypto;

import be.distrinet.spite.iotsear.ProviderSPI;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class HashSPI implements ProviderSPI {
    /**
     * Convenience method. Converts the data String (UTF-8) to a byte array after which the 'byte[] doHash(byte[] data)' method is called
     * @param data
     * @return A base64 encoding of the hash output
     */
    public String doHash(String data){
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a hash of the given data
     * @param data
     * @return the hash output as a byte-array
     */
    public abstract byte[] doHash(byte[] data);
}
