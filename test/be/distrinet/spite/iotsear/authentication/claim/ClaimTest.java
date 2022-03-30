package be.distrinet.spite.iotsear.authentication.claim;

import be.distrinet.spite.iotsear.core.exceptions.VerificationException;
import be.distrinet.spite.iotsear.core.model.Attribute;
import be.distrinet.spite.iotsear.core.model.credential.Credential;
import be.distrinet.spite.iotsear.core.model.credential.CredentialType;
import be.distrinet.spite.iotsear.core.model.credential.Secret;
import be.distrinet.spite.iotsear.core.model.credential.issuance.Issuer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


class ClaimTest {
    Claim claim;
    Credential cred;

    @Before
    void setUp() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("b", "string");
        attributes.put("a", "string");
        final Issuer issuer = new Issuer() {
            @Override
            public boolean equals(final Issuer otherIssuer) {
                return true;
            }

            @Override
            public String getProviderID() {
                return "sss";
            }
        };
        final CredentialType type = new CredentialType(attributes, issuer);
        final CredentialBlock block = new CredentialBlock(type);
        final List<CredentialBlock> cb = new ArrayList<>();
        cb.add(block);
        final List<PredicateBlock> pb = new ArrayList<>();
        this.claim = new Claim(cb, pb);

        final List<Attribute> atts = new ArrayList<>();
        atts.add(new Attribute() {

            @Override
            public String getValue() {
                return "b";
            }

            @Override
            public String getType() {
                return "b";
            }


        });
        atts.add(new Attribute() {


            @Override
            public String getValue() {
                return "a";
            }

            @Override
            public String getType() {
                return "a";
            }

        });

        this.cred = new Credential(atts, type) {
            @Override
            public void verify() throws VerificationException {

            }

            @Override
            public boolean checkSecret(final Secret secret) {
                return false;
            }
        };
    }

    @After
    void tearDown() {
        this.claim = null;
    }

    @Test
    void initializeClaim() {
        final List<Credential> myList = new ArrayList<>();
        myList.add(this.cred);
        this.claim.initializeClaim(myList);
        for (final Credential cred : this.claim.getCredentialMap().values()) {
            assertNotNull(cred);
        }
    }

    @Test
    void clearCredentials() {
        initializeClaim();
        this.claim.clearCredentials();
        for (final Credential cred : this.claim.getCredentialMap().values()) {
            assertNull(cred);
        }
    }


    @Test
    void setCredential() {
        assertTrue(this.claim.setCredential(this.cred));
    }

    @Test
    void matchCredential() {
        assertEquals(new ArrayList<CredentialBlock>(this.claim.getCredentialMap().keySet()).get(0), this.claim.matchCredential(this.cred));
    }

    @Test
    void getProviderID() {
        assertEquals("sss", this.claim.getProviderID());
    }
}