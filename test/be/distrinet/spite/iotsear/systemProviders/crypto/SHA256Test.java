package be.distrinet.spite.iotsear.systemProviders.crypto;


import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;


class SHA256Test {

    String testData = "DIT IS EEN TEST";
    String testResultHex = "fe4d9dd21897b4125ce41ff20ee31cff5cc5bae58c4cae17162ae469712d621a";
    String largeResultHex = "61e358ae99e3b3c27dad7e3bd3ebc4752b8fdae2faabbd5486f6bfdf4d2f02c0";
    String largeData = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    SHA256 sha256 = new SHA256();

    @Test
    void doHash() {
        final byte[] data1 = this.sha256.doHash(this.testData.getBytes());
        final byte[] data2 = this.sha256.doHash(this.largeData.getBytes());
        final String hex1 = new BigInteger(1, data1).toString(16);
        final String hex2 = new BigInteger(1, data2).toString(16);

        assertEquals(hex1, this.testResultHex);
        assertEquals(hex2, this.largeResultHex);

        final byte[] data11 = this.sha256.doHash(this.testData.getBytes());
        final byte[] data22 = this.sha256.doHash(this.largeData.getBytes());
        final String hex11 = new BigInteger(1, data11).toString(16);
        final String hex22 = new BigInteger(1, data22).toString(16);

        assertEquals(hex11, hex1);
        assertEquals(hex22, hex2);

        assertEquals(data1.length, 256 / 8);
        assertEquals(data2.length, 256 / 8);
    }
}