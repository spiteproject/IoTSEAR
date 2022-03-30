# IoTSEAR

## IoTSEAR usage

In order to use/compile, you need to import only the IoTSEAR.jar as a dependency.  
However, to run the program, you'll need to include all other jar files in the classpath.

Using IoTSEAR in a program is fairly easy and straightforward,
however, it does rely on a lot of configuration settings, which are specified in a separate config file.

## Examples & important API

### IoTSEAR

This is a singleton object which gives access to the important IoTSEAR components:

- PolicyEngine: responsible for evaluating policies
- PolicyRepository
- ContextStore

```java
IoTSEAR ioTSEAR = IoTSEAR.getInstance();
ioTSEAR.configure(new File("./IoTSEAR.json"));
//ContextStorage store = iotSEAR.getContextStore();
//PolicyRepository repo = iotSEAR.getPolicyRepository();
PolicyEngine engine = ioTSEAR.getPolicyEngine();
AuthorizationPolicy.PolicyEffect effect = engine.enforce("anysubject",
                                                        "anyresource",
                                                        "anyaction");
```

Below is a configuration example of "./IoTSEAR.json":

```json
{
  "pbms": {
    "repository": {
      "provider": "iotsear:pbms:directory-based-policy-repository",
      "path": "./testPolicies/",
      "cache": false,
      "parser": "iotsear:pbms:darcJsonParser"
    },
    "pdp": "iotsear:pbms:localPDP",
    "context-persistence": {
      "provider": "iotsear:contextStore:memory"
    }
  },
  "sources": [
    {
      "provider": "sources:mock",
      "identifier": "myProximitySensor",
      "contextHandler": {
        "provider": "iotsear:pbms:mqttReceiver",
        "topic": "myApp/proximity",
        "broker": "tcp://localhost:1883",
        "decoder": "iotsear:context:encoders:string-encoder-decoder"
      },
      "metadata": {
        "contextType": "presence",
        "proofSupport": [
          "iotsear:crypto:hash:sha256",
          "iotsear:crypto:sign:rsa2048"
        ]
      }
    },
    {
      "provider": "sources:mock",
      "identifier": "myDoorbell",
      "contextHandler": {
        "provider": "iotsear:pbms:mqttReceiver",
        "topic": "myApp/doorbell",
        "broker": "tcp://localhost:1883",
        "decoder": "iotsear:context:encoders:string-encoder-decoder"
      },
      "metadata": {
        "contextType": "presence"
      }
    },
    {
      "provider": "sources:mock",
      "identifier": "myLightSensor",
      "contextHandler": {
        "provider": "iotsear:pbms:mqttReceiver",
        "topic": "myApp/light",
        "broker": "tcp://localhost:1883",
        "decoder": "iotsear:context:encoders:string-encoder-decoder"
      },
      "metadata": {
        "contextType": "light"
      }
    }
  ]
}
```

All strings with the following structure: "x:y:z:..." identify framework plugins.
It is possible to create your own plugins, or extend existing ones, and make IoTSEAR load them
by changing these identifiers. This process will be explained later on.

It is probably not necessary to change the 'pbms' section.

The 'devices' section, however, should be different for each application. Some things to note:

1. each identifier must be unique
2. w.r.t. the mqttReceiver: it is important that each device uses a different topic.
3. sources should have a 'contextType' metadata-property. This is used for ContextAttribute.getType()

### ContextAttribute

```java
//constructor
public ContextAttribute(final String type, final String value);
/**
* Gets the value of this attribute.
*/
public String getValue();
/**
* Returns the type of this context attribute.
* This is derived from the source-type if the source of this attribute has been set.
*
* @return this attribute's type, or an empty string if the attribute type is not known.
*/
public String getType();
public void putMetaData(final String type, final String value);
public String getMetaData(final String type);
public long getTimestamp();
public void setTimestamp(final long timestamp);
public Map<String, String> getMetaData();
/**
* Gets the source/device that is associated with this context attribute,
* or null if the source is not known/set.
*/
public ContextSource getSource();
public void setSource(final ContextSource source);
public Subject getSubject();
public void setSubject(final Subject subject);
```

### ContextSource

The context-source represents the device which produced a context-reading. The ContextSource class is a framework extension point. One extension is available as a system provider: MockDevice  
Each ContextSource has a ContextHandler, which is responsible for accessing the device itself/retrieving the context. ContextHandler is also a framework extension point, and one extension is available as system provider: MqttContextHandler. This handler will automatically add context to the configured IoTSEAR-contextstore

```java
/**
* attempts to retrieve source output.
* this method is asynchronous. The configured listeners will be notified when the source responds.
*/
public abstract void retrieveContext();

/**
* attempts to retrieve source output.
* this method is asynchronous. ONLY the provided listener will be notified when the source responds.
*/
public abstract void retrieveContext(ContextListener listener);
public void putMetaData(final String type, final String value);
public String getMetaData(final String type);
public Map<String, String> getMetaData();
public String getIdentifier();
public String getSourceType();
public void setSourceType(final String type);
public String getProviderID();
/**
* factory method to create a new Context Source object from the IoTSEAR configuration
*
* @param json
* @return
*/
public ContextSource createSource(JSONObject json);
public void setContextHandler(final ContextHandler handler);
/**
* add a listener that will be called when context from this device is received.
*
* @param listener
*/
public void addContextListener(final ContextListener listener);
```

A ContextListener is a simple callback which can be added to a ContextSource with ContextSource.addContextListener(listener).

```java
public interface ContextListener {
    void processContext(ContextAttribute attribute);
}
```

### ContextManager

in package: be.distrinet.spite.iotsear.managers  
This is an important plugin-manager which manages context-sources, context-stores, context-handlers and encoders-decoders.

```java
public static ContextManager getInstance()

public ContextSource getSource(final String identifier);
public List<ContextSource> getSourcesByType(final String type);
public ContextStorage getContextStore(final String providerID);
public ContextHandler getContextHandler(final String providerID);
public ContextEncoder getEncoder(final String providerID);
public ContextDecoder getDecoder(final String providerID);
```

### ContextStore

```java
/**
 * Store a context attribute in this storage
 *
 * @param attribute the context attribute
 */
void store(ContextAttribute attribute);

/**
 * Delete the attributes that match with the given selector from this storage
 *
 * @param selector
 */
void prune(ContextSelector selector);

/**
 * Find the list of contextattributes given the context selector
 */
List<ContextAttribute> find(ContextSelector selector);

/**
 * Find the list of contextattributes given the subject-identifier
 */
List<ContextAttribute> findBySubject(String subjectID);

/**
 * Find the list of contextattributes given the source-identifier
 */
List<ContextAttribute> findBySource(String sourceID);

/**
 * Find the list of contextattributes given the source-type
 */
List<ContextAttribute> findBySourceType(String SourceType);
```

At time of writing, the only context-store is an in-memory store --> the context-attributes
are lost when the app restarts.
The prune method is not implemented.

One that depends on a database-backend is being worked on.

### PolicyRepository

```java
/**
     * Retrieve a specific policy
     *
     * @param policyID the identifier of the desired policy
     * @return the policy, or null if it is not found
     */
    AuthorizationPolicy retrievePolicy(String policyID);

    /**
     * Retrieve the list of all policy-identifiers in this repository
     */
    List<String> getAllPolicyIDs();

    /**
     * Retrieve all policies in this repository
     *
     * @return the list of all policies in this repository
     */
    List<AuthorizationPolicy> retrievePolicies();

    /**
     * Store a new policy in this repository.
     * This operation will not succeed if a policy with the same identifier is already present (see #storePolicy)
     *
     * @param policy the policy
     * @return true if the operation succeeds, false otherwise.
     */
    boolean storePolicy(AuthorizationPolicy policy);

    /**
     * Update an existing policy in this repository.
     * This operation will not succeed if this repository does not already contain a policy with the same identifier.
     *
     * @param policy the policy
     * @return true if the operation succeeds, false otherwise.
     */
    boolean updatePolicy(AuthorizationPolicy policy);

    /**
     * Delete an existing policy in this repository
     *
     * @param policyID the identifier of the policy that should be deleted
     * @return true if the operation succeeds, false otherwise.
     */
    boolean deletePolicy(String policyID);

```

There is one provider implemented at the moment: A directory-based repository.  
store, update & delete is not implemented at the moment, but these features have a high priority...

IMPORTANT: THE FILENAMES MUST CORRESPOND TO [policyID].json

## Policies

example:

```json
{
  "identifier": "myPolicy3",
  "priority": 2,
  "target": {
    "subject": "AnySubject",
    "resource": "AnyResource",
    "action": "AnyAction"
  },
  "effect": "allow",
  "condition": {
    "darc:condition:and": [
      {
        "source": "myDoorbell",
        "operation": "darc:condition:operation:equals",
        "value": "1",
        "verifiers": ["darc:condition:verifier:freshness:30s"]
      },
      {
        "source": "presence",
        "operation": "darc:condition:operation:equals",
        "value": "1",
        "verifiers": [
          "darc:condition:verifier:freshness:30s",
          "darc:condition:verifier:security:substantial"
        ]
      },
      {
        "darc:condition:or": [
          {
            "source": "mySensorID1",
            "operation": "darc:condition:operation:lt",
            "value": "5",
            "verifiers": ["darc:condition:verifier:freshness:30s"]
          },
          {
            "source": "mySensorID2",
            "operation": "darc:condition:operation:geq",
            "value": "4",
            "verifiers": ["darc:condition:verifier:freshness:1m"]
          }
        ]
      }
    ]
  }
}
```

example with comments:

```json
{
  "identifier": "myPolicy3", //identifiers should be unique
  "priority": 2, //a lower number corresponds to a higher priority. 0 is the highest priority possible.
  "target": {
    "subject": "AnySubject", //if the person is not yet authenticated, subject=AnySubject
    "resource": "AnyResource", //AnyResource --> wildcard
    "action": "AnyAction" //AnyAction --> wildcard
  },
  "effect": "allow", //(allow/deny) IoTSEAR is default-deny, so most of the policies should allow
  "condition": {
    //A condition is either one single condition-object, or an and/or condition-array.
    //Each element in these arrays are either a condition-object or a new and/or condition-array.
    "darc:condition:and": [
      {
        "source": "myDoorbell", //the source is either the source-identifier, or a source-type
        "operation": "darc:condition:operation:equals", //the operation is a plugin-identifier
        "value": "1", //all values are encoded as strings
        "verifiers": [
          "darc:condition:verifier:freshness:30s" //multiple verifiers can be defined here
        ]
      },
      {
        "source": "presence",
        "operation": "darc:condition:operation:equals",
        "value": "1",
        "verifiers": [
          "darc:condition:verifier:freshness:30s",
          "darc:condition:verifier:security:substantial"
          //darc:condition:verifier:security:substantial is a custom verifier, it is not provided by default in IoTSEAR
        ]
      },
      {
        "darc:condition:or": [
          {
            "source": "mySensorID1",
            "operation": "darc:condition:operation:lt",
            "value": "5",
            "verifiers": ["darc:condition:verifier:freshness:30s"]
          },
          {
            "source": "mySensorID2",
            "operation": "darc:condition:operation:geq",
            "value": "4",
            "verifiers": ["darc:condition:verifier:freshness:1m"]
          }
        ]
      }
    ]
  }
}
```

## Extensions

IoTSEAR is intended to be extended by the application, however, several default providers are available:

| identifier                                     | Class                          | Extension Point                                            |
| ---------------------------------------------- | ------------------------------ | ---------------------------------------------------------- |
| darc:condition:and                             | ConditionSetAND                | PolicyConditionSet & PolicyConditionSetFactory             |
| darc:condition:or                              | ConditionSetOR                 | PolicyConditionSet & PolicyConditionSetFactory             |
| darc:condition:verifier:freshness:30s          | FreshnessVerifier30s           | PolicyConditionVerifier & PolicyConditionVerifierFactory   |
| darc:condition:verifier:freshness:1m           | FreshnessVerifier1m            | PolicyConditionVerifier & PolicyConditionVerifierFactory   |
| darc:condition:verifier:freshness:5m           | FreshnessVerifier5m            | PolicyConditionVerifier & PolicyConditionVerifierFactory   |
| darc:condition:operation:equals                | EqualityOperation              | PolicyConditionOperation & PolicyConditionOperationFactory |
| darc:condition:operation:geq                   | GreaterThanOrEqualsOperation   | PolicyConditionOperation & PolicyConditionOperationFactory |
| darc:condition:operation:gt                    | GreatherThanOperation          | PolicyConditionOperation & PolicyConditionOperationFactory |
| darc:condition:operation:lt                    | LessThanOperation              | PolicyConditionOperation & PolicyConditionOperationFactory |
| darc:condition:operation:leq                   | LessThanOrEqualsOperation      | PolicyConditionOperation & PolicyConditionOperationFactory |
| iotsear:contextStore:memory                    | ContextMemoryStorage           | ContextStorage                                             |
| iotsear:pbms:directory-based-policy-repository | DirectoryBasedPolicyRepository | PolicyRepository                                           |
| iotsear:pbms:localPDP                          | LocalPDP                       | PDP                                                        |
| iotsear:pbms:mqttReceiver                      | MqttContextHandler             | ContextHandler                                             |

If you want to create your own plugins, it is as simple as:

1. Creating a new class
2. Annoting the class with the '@Extension' annotation
3. implement/extend the ExtensionPoints
4. use the new identifiers in the policies/config file

!IMPORTANT!, make sure that "annotation processing" compiler setting is enabled in your IDE, this is usually disabled by default...

see https://github.com/pf4j/pf4j for more info

example:

```java
import be.distrinet.spite.iotsear.core.model.context.ContextAttribute;
import be.distrinet.spite.iotsear.policy.PolicyConditionVerifier;
import be.distrinet.spite.iotsear.policy.abstractFactories.PolicyConditionVerifierFactory;
import org.pf4j.Extension;

@Extension
public class HighSecurityVerifier extends PolicyConditionVerifier implements PolicyConditionVerifierFactory {
    @Override
    public boolean verify(ContextAttribute contextAttribute) {
// cusom code to verify the elegibility of a context attribute goes here
    }

    @Override
    public PolicyConditionVerifier createPolicyConditionVerifier() {
        return new HighSecurityVerifier();
    }

    @Override
    public String getProviderID() {
        return "darc:condition:verifier:security:high";
    }
}

```

## Logging

IoTSEAR uses the flogger API for its logging https://github.com/google/flogger

IoTSEAR logs every extension that is loaded. You can verify whether or not IoTSEAR loads your custom extension by inspecting these logs.  
In addition, the MqttContextHandler logs context that it receives, and the PolicyEngine logs the policies that are satisfied. Finally, errors and unexpected events are also logged.

You can also use flogger by including all the IoTSEAR dependencies to your project. The usage is straightforward:

```java
//static field declaration, put this at the top of the class in which you want to log something
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();


//usage
this.logger.atInfo().log("initializing with config: \n" + config.toJSONString());
//logging exceptions
catch (final MqttException e) {
    logger.atSevere().withCause(e).log("mqtt exception, is the broker running?");
}
```
