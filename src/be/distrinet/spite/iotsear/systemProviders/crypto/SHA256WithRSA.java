package be.distrinet.spite.iotsear.systemProviders.crypto;

import be.distrinet.spite.iotsear.core.exceptions.InvalidConfigurationException;
import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.crypto.SignatureSPI;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Extension
public class SHA256WithRSA extends SignatureSPI {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyPair generateKey(final int keySize) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            final KeyPair pair = keyGen.generateKeyPair();
            return pair;
        } catch (final NoSuchAlgorithmException e) {
            logger.atSevere().withCause(e).log("This JRE does not provide the RSA algorithm");
        }
        return null;
    }

    @Override
    public byte[] sign(byte[] data) throws InvalidSignatureException {
        try {
            if(this.privateKey==null)throw new InvalidKeyException("key not initialized");
            final Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(this.privateKey);
            privateSignature.update(data);
            return privateSignature.sign();
        } catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            logger.atSevere().withCause(e).log("");
            throw new InvalidSignatureException(e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) throws InvalidSignatureException {
        try {
            if(this.publicKey==null)return false;
            final Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(publicKey);
            publicSignature.update(data);
            return publicSignature.verify(signature);
        } catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            logger.atSevere().withCause(e).log("");
            throw new InvalidSignatureException(e);
        }
    }

    @Override
    public SHA256WithRSA configure(JSONObject jsonObject) throws InvalidConfigurationException {
        SHA256WithRSA prototype = new SHA256WithRSA();
        prototype.setIdentifier(jsonObject.get("identifier").toString());
        try {
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            if(jsonObject.containsKey("private-key")){
                byte[] bytes = processFileOrString(jsonObject.get("private-key").toString());
                /* Generate private key. */
                PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                prototype.privateKey = kf.generatePrivate(ks);
            }
            if(jsonObject.containsKey("public-key")) {
                byte[] bytes = processFileOrString(jsonObject.get("public-key").toString());

                /* Generate public key. */
                X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                prototype.publicKey = kf.generatePublic(ks);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            logger.atSevere().withCause(e).log("");
            throw new InvalidConfigurationException(e.getMessage());
        }
        return prototype;
    }

    private byte[] processFileOrString(String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            return Base64.getDecoder().decode(path);
        }else {
            return Files.readAllBytes(Paths.get(path));
        }
    }

    @Override
    public String getProviderID() {
        return "iotsear:crypto:sign:sha256withrsa";
    }

    public static void main(String[] args) {
        SHA256WithRSA sha = new SHA256WithRSA();
        JSONObject json = new JSONObject();

        KeyPair kp = sha.generateKey(2048);


        saveKeysToDiskBase64(kp);

    }


    public static void saveKeysToDiskBase64( KeyPair keyPair) {
        try {
            File f = new File("./key.private");
            File f2 = new File("./key.public");


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(keyPair.getPublic().getEncoded());
            fos.flush();
            fos.close();

            fos = new FileOutputStream(f2);
            fos.write(keyPair.getPrivate().getEncoded());
            fos.flush();
            fos.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
