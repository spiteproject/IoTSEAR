package be.distrinet.spite.iotsear.systemProviders.crypto;

import be.distrinet.spite.iotsear.core.exceptions.InvalidConfigurationException;
import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import be.distrinet.spite.iotsear.crypto.SignatureSPI;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONObject;
import org.pf4j.Extension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Extension
public class SHA256WithHMAC extends SignatureSPI {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final static String ALGORITHM = "HmacSHA256";

    private SecretKeySpec secretKey;

    public SecretKeySpec generateKey(final int keySize) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[keySize];
        random.nextBytes(key);
        return new SecretKeySpec(key, ALGORITHM);
    }

    public SHA256WithHMAC(){}
    public SHA256WithHMAC(String secretKeyB64){
        this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyB64),ALGORITHM);
    }

    @Override
    public byte[] sign(byte[] data) throws InvalidSignatureException {
        try {
            if(this.secretKey==null) throw new InvalidKeyException("key not initialized");

            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(this.secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new InvalidSignatureException(e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) throws InvalidSignatureException {
        byte[] control = this.sign(data);
        return Arrays.equals(control,signature);
    }

    @Override
    public SHA256WithHMAC configure(JSONObject jsonObject) throws InvalidConfigurationException {
        SHA256WithHMAC prototype = new SHA256WithHMAC();
        prototype.setIdentifier(jsonObject.get("identifier").toString());

        if(!jsonObject.containsKey("secret-key")) {
            throw new InvalidConfigurationException("secret-key field not specified");
        }
        byte[] key = Base64.getDecoder().decode(jsonObject.get("secret-key").toString());
        prototype.secretKey = new SecretKeySpec(key, ALGORITHM);

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
        return "iotsear:crypto:sign:sha256withhmac";
    }

    public static void main(String[] args) throws InvalidSignatureException {
        SHA256WithHMAC sha = new SHA256WithHMAC();
        SecretKeySpec key = sha.generateKey(32);
        sha.secretKey = key;
        System.out.println(new String(Base64.getEncoder().encode(key.getEncoded()), StandardCharsets.UTF_8));

    }

}
