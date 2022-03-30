package be.distrinet.spite.iotsear.systemProviders.context.encoders;

import be.distrinet.spite.iotsear.core.model.context.*;
import be.distrinet.spite.iotsear.managers.ContextManager;
import com.google.common.flogger.FluentLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.pf4j.Extension;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Extension
public class JsonStringEncoderDecoder implements ContextEncoder, ContextDecoder {
    private static final Charset charSet = StandardCharsets.UTF_8;
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @Override
    public ContextAttribute decode(final byte[] bytes) {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final InputStreamReader reader = new InputStreamReader(inputStream);
        final JSONParser parser = new JSONParser();
        try {
            final JSONObject json = (JSONObject) parser.parse(reader);
            return this.fromJSON(json);
        } catch (final Exception e) {
            logger.atSevere().withCause(e).log("unable to decode context object");
            return null;
        }
    }

    @Override
    public byte[] encode(final ContextAttribute attribute) {
        final JSONObject json = this.toJSON(attribute);
        return json.toString().getBytes(charSet);
    }

    @Override
    public String getProviderID() {
        return "iotsear:context:encoders:string-encoder-decoder";
    }

    public ContextAttribute fromJSON(final JSONObject json) {
        final ContextAttribute attribute = new ContextAttribute(json.get("type").toString(), json.get("value").toString());

        if (json.containsKey("metadata")) {
            final JSONObject tmp = (JSONObject) json.get("metadata");
            for (final Object entry : tmp.entrySet()) {
                final Map.Entry e = (Map.Entry) entry;
                attribute.putMetaData(e.getKey().toString(), e.getValue().toString());
            }
        }

        if (json.containsKey("subject")) {
            final JSONObject subject = (JSONObject) json.get("subject");
            if (subject.containsKey("environment")) {
                JSONArray env = (JSONArray) subject.get("environment");
                List<ContextSource> envSources= new ArrayList<>();
                for(Object source:env){
                    envSources.add(ContextManager.getInstance().getSource(source.toString()));
                }
                attribute.setSubject(new Environment(subject.get("identifier").toString(), envSources));
            } else {
                attribute.setSubject(new Entity(subject.get("identifier").toString()));
            }
        }

        if (json.containsKey("source")) {
            final String source = json.get("source").toString();
            final ContextSource theSource = ContextManager.getInstance().getSource(source);
            attribute.setSource(theSource);
        }
        return attribute;
    }

    public JSONObject toJSON(final ContextAttribute attribute) {
        final JSONObject json = new JSONObject();

        json.put("type", attribute.getType());
        json.put("value", attribute.getValue());
        final JSONObject attributeMetaData = new JSONObject();
        final Map<String, String> metaData = attribute.getMetaData();
        attributeMetaData.putAll(metaData);
        json.put("metadata", metaData);

        if (attribute.getSubject() != null) {
            final JSONObject subject = new JSONObject();
            if(attribute.getSubject() instanceof Environment){
                JSONArray envDevices = new JSONArray();
                for(ContextSource source:((Environment)attribute.getSubject()).getEnvironmentDevices()){
                    //envDevices.add(sourceToJSON(source));
                    envDevices.add(source.getIdentifier());
                }
                subject.put("environment",envDevices);
            }
            subject.put("identifier", attribute.getSubject().getIdentifier());
            json.put("subject", subject);
        }

        ContextSource source = attribute.getSource();
        if (source != null) {
            //json.put(source, sourceToJSON(source));
            json.put("source", source.getIdentifier());
        }
        return json;
    }

//    private JSONObject sourceToJSON(ContextSource source) {
//        JSONObject json = new JSONObject();
//        json.put("identifier", source.getIdentifier());
//        json.put("type", source.getProviderID());
//        JSONObject sourceMD = new JSONObject();
//        sourceMD.putAll(source.getMetaData());
//        json.put("metadata", sourceMD);
//        json.put("source", source);
//        return json;
//    }
}
