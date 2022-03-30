package be.distrinet.spite.iotsear.systemProviders.context.encoders;

import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.core.model.context.ContextDecoder;
import be.distrinet.spite.iotsear.core.model.context.ContextEncoder;
import be.distrinet.spite.iotsear.core.model.context.ContextStructure;
import be.distrinet.spite.iotsear.managers.ContextManager;
import com.google.protobuf.InvalidProtocolBufferException;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class ProtobufEncoderDecoder implements ContextEncoder, ContextDecoder {
    @Override
    public ContextAttribute decode(final byte[] bytes) {
        try {
            final ContextStructure.ContextBuffer structure = ContextStructure.ContextBuffer.parseFrom(bytes);
            final ContextAttribute attribute = new ContextAttribute(structure.getAttributeName(), structure.getValue());
            for (final ContextStructure.ContextBuffer.MetaData md : structure.getAttributeMetadataList()) {
                attribute.putMetaData(md.getType(), md.getValue());
            }
            if (structure.hasSource()) {
                attribute.setSource(ContextManager.getInstance().getSource(structure.getSource()));
            }
            if (structure.hasSubject()) {
                //FIXME
            }
            return attribute;
        } catch (final InvalidProtocolBufferException  e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public byte[] encode(final ContextAttribute attribute) {
        final ContextStructure.ContextBuffer.Builder structBuilder = ContextStructure.ContextBuffer.newBuilder()
                .setAttributeName(attribute.getType())
                .setValue(attribute.getValue());
        if (attribute.getSource() != null) {
            structBuilder.setSource(attribute.getSource().getIdentifier());
        }
        if (attribute.getSubject() != null) {
            structBuilder.
                    setSubject(ContextStructure.ContextBuffer.Subject.newBuilder().setIdentifier(attribute.getSource().getIdentifier()));
        }

        int index = 0;
        for (final Map.Entry<String, String> metadataEntry : attribute.getMetaData().entrySet()) {
            final ContextStructure.ContextBuffer.MetaData.Builder mdBuilder = ContextStructure.ContextBuffer.MetaData.newBuilder();
            structBuilder.setAttributeMetadata(index, mdBuilder.setType(metadataEntry.getKey())
                    .setValue(metadataEntry.getValue()).build());
            index++;
        }

        final ContextStructure.ContextBuffer buff = structBuilder.build();

        return buff.toByteArray();
    }

    @Override
    public String getProviderID() {
        return "iotsear:context:encoders:protobuf-encoder-decoder";
    }
}
