package be.distrinet.spite.iotsear.crypto;

import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.managers.CryptoManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


class CryptoManagerTest {

    @Test
    void doHash() throws ProviderNotFoundException {
        final byte[] bytes = "test".getBytes();
        final byte[] result = CryptoManager.getInstance().getHashProvider(CryptoManager.HASH_SHA265_SYSTEM_PROVIDER).doHash(bytes);
        assertNotNull(result);
        assertEquals(result.length, 256 / 8);
    }

}