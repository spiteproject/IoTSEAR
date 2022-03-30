package be.distrinet.spite.iotsear.systemProviders.crypto;

import be.distrinet.spite.iotsear.core.exceptions.ProviderException;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


class SHA256WithRSATest {
    private final String testString = "lorem ipsum something somtehing";
    private SHA256WithRSA rsaProvider;
    private KeyPair keyPair;

    @Before
    void setup() {
        this.rsaProvider = new SHA256WithRSA();
        this.keyPair = this.rsaProvider.generateKey(2048);
    }

    @Test
    void generateKey() {
        KeyPair kp = this.rsaProvider.generateKey(1024);
        PublicKey publicKey = (PublicKey) kp.getPublic();

        assertEquals(((RSAKey)publicKey).getModulus().bitLength(), 1024);
        assertEquals(publicKey.getAlgorithm(), "RSA");

        kp = this.rsaProvider.generateKey(2048);
        publicKey = (PublicKey) kp.getPublic();
        assertEquals(((RSAKey)publicKey).getModulus().bitLength(), 2048);
        assertEquals(publicKey.getAlgorithm(), "RSA");

        kp = this.rsaProvider.generateKey(4092);
        publicKey = (PublicKey) kp.getPublic();
    }

    @Test
    void signVerify() throws ProviderException {
//        final byte[] signature = this.rsaProvider.sign(this.testString, this.keyPair.getPrivate());
//        assertEquals(signature.length, 2048 / 8);
//        assertTrue(this.rsaProvider.verify(this.testString, signature, this.keyPair.getPublic()));
    }


}