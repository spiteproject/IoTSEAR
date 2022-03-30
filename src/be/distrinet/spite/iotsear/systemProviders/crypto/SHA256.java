package be.distrinet.spite.iotsear.systemProviders.crypto;

import be.distrinet.spite.iotsear.crypto.HashSPI;
import com.google.common.flogger.FluentLogger;
import org.pf4j.Extension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Extension
public class SHA256 extends HashSPI {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public byte[] doHash(final byte[] data) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(data);
            return digest.digest();
        } catch (final NoSuchAlgorithmException e) {
            logger.atSevere().withCause(e).log("This JRE does not provide the SHA 256 hash algorithm");
        }
        return new byte[0];
    }

    @Override
    public String getProviderID() {
        return "iotsear:crypto:hash:sha256";
    }
}
