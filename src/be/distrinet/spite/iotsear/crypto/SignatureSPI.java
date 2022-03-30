package be.distrinet.spite.iotsear.crypto;

import be.distrinet.spite.iotsear.ProviderSPI;
import be.distrinet.spite.iotsear.core.exceptions.InvalidConfigurationException;
import be.distrinet.spite.iotsear.core.exceptions.InvalidSignatureException;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class SignatureSPI implements ProviderSPI {
    private String identifier="";

    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }
    public String getIdentifier(){
        return identifier;
    }
    public abstract byte[] sign(byte[] data) throws InvalidSignatureException;
    public abstract boolean verify(byte[] data, byte[] signature) throws InvalidSignatureException;

    public abstract SignatureSPI configure(JSONObject jsonObject) throws InvalidConfigurationException;

    public String sign(String data) throws InvalidSignatureException {
        byte[] signature = this.sign(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String data, String signature) throws InvalidSignatureException{
        Base64.Decoder decoder = Base64.getDecoder();
        return this.verify(data.getBytes(StandardCharsets.UTF_8), decoder.decode(signature));
    }
}
