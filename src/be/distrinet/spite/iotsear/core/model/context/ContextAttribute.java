package be.distrinet.spite.iotsear.core.model.context;

import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.Subject;
import be.distrinet.spite.iotsear.core.model.context.proof.AuthenticityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.IntegrityProof;
import be.distrinet.spite.iotsear.core.model.context.proof.OwnershipProof;
import be.distrinet.spite.iotsear.managers.ProofManager;

import java.util.HashMap;
import java.util.Map;

public class ContextAttribute implements Attribute {
    private final String type;
    private final String value;
    private final Map<String, String> attributeMetaData = new HashMap<>();
    private IntegrityProof integrityProof;
    private AuthenticityProof authenticityProof;
    private OwnershipProof ownershipProof;

    private ContextSource source;
    private Subject subject;

    public final static String TS = "_timestamp_";
    public final static String IP = "_integrity_proof_";
    public final static String IPP = "_integrity_proof_provider_";
    public final static String AP = "_authenticity_proof_";
    public final static String APP = "_authenticity_proof_provider_";
    public final static String OP = "_ownership_proof_";
    public final static String OPP = "_ownership_proof_provider_";
    public final static String SEPARATOR = "###";


    public ContextAttribute(final String type, final String value) {
        this.type = type;
        this.value = value;
    }

    public void putMetaData(final String type, final String value) {
        this.attributeMetaData.put(type, value);
    }

    public String getMetaData(final String type) {
        return this.attributeMetaData.get(type);
    }

    public Map<String, String> getMetaData() {
        return new HashMap<>(this.attributeMetaData);
    }

    /**
     * Gets the source/device that is associated with this context attribute, or null if the source is not known/set.
     */
    public ContextSource getSource() {
        return this.source;
    }

    public void setSource(final ContextSource source) {
        this.source = source;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the type of this context attribute. This is derived from the source-type.
     *
     * @return this attribute's type, or an empty string if the attribute type is not known.
     */
    @Override
    public String getType() {
        if (this.source != null) {
            final String sourceType = this.source.getSourceType();
            if (sourceType != null) {
                return sourceType;
            }
        }
        return this.type;
    }

    /**
     * Gets the subject that is associated with this context attribute, or null if the subject is not known/set.
     */
    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(final Subject subject) {
        this.subject = subject;
    }

    public long getTimestamp() {
        return Long.parseLong(getMetaData(TS));
    }

    public void setTimestamp(final long timestamp) {
        putMetaData(TS, Long.toString(timestamp));
    }

    public IntegrityProof getIntegrityProofFromMetadata() {
        if(integrityProof==null) {
            String integrityString = getMetaData(IP);
            if (integrityString == null) return null;
            String[] split = integrityString.split(SEPARATOR);
            try {
                this.integrityProof = ProofManager.getInstance().getIntegrityProof(split[0],split[1],this);
            } catch (ProviderNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return this.integrityProof;
    }

    public void setIntegrityProof(final IntegrityProof proof) {
        putMetaData(IP, proof.getProviderID()+SEPARATOR+proof.getProofString());
        //set proof to null in order to force the proof object to be created again
        //with this object as context attribute
        this.integrityProof = null;
    }

    public AuthenticityProof getAuthenticityProofFromMetadata() {
        if(authenticityProof==null) {
            String authenticityString = getMetaData(AP);
            if (authenticityString == null) return null;
            String[] split = authenticityString.split(SEPARATOR);
            try {
                this.authenticityProof = ProofManager.getInstance().getAuthenticityProof(split[0],split[1],this);
            } catch (ProviderNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return this.authenticityProof;
    }

    public void setAuthenticityProof(final AuthenticityProof proof) {
        putMetaData(AP, proof.getProviderID()+SEPARATOR+proof.getProofString());
        //set proof to null in order to force the proof object to be created again
        //with this object as context attribute
        this.authenticityProof = null;
    }

    public OwnershipProof getOwnershipProofFromMetadata() {
        if(ownershipProof==null) {
            String ownershipString = getMetaData(OP);
            if (ownershipString == null) return null;
            String[] split = ownershipString.split(SEPARATOR);
            try {
                this.ownershipProof = ProofManager.getInstance().getOwnershipProof(split[0],split[1],this);
            } catch (ProviderNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return this.ownershipProof;
    }

    public void setOwnershipProof(final OwnershipProof proof) {
        putMetaData(OP, proof.getProviderID()+SEPARATOR+proof.getProofString());
        //set proof to null in order to force the proof object to be created again
        //with this object as context attribute
        this.ownershipProof = null;
    }
}
