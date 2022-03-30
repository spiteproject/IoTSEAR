package be.distrinet.spite.iotsear.systemProviders.darc;


import be.distrinet.spite.iotsear.core.exceptions.ProviderException;
import be.distrinet.spite.iotsear.core.exceptions.ProviderNotFoundException;
import be.distrinet.spite.iotsear.managers.PolicyFactoryManager;
import be.distrinet.spite.iotsear.pbms.PolicyParser;
import be.distrinet.spite.iotsear.policy.AuthorizationPolicy;
import be.distrinet.spite.iotsear.policy.PolicyCondition;
import be.distrinet.spite.iotsear.policy.PolicyConditionSet;
import be.distrinet.spite.iotsear.policy.PolicyTarget;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class DarcJSONParser implements PolicyParser {


    @Override
    public AuthorizationPolicy parsePolicy(final String json) throws ProviderException {
        final JSONParser parser = new JSONParser();
        try (final Reader reader = new StringReader(json)) {
            final JSONObject jsonObject = (JSONObject) parser.parse(reader);
            final String identifier = jsonObject.get("identifier").toString();
            final int priority = Integer.parseInt(jsonObject.get("priority").toString());
            final PolicyTarget target = getTarget((JSONObject) jsonObject.get("target"));
            final AuthorizationPolicy.PolicyEffect effect = getEffect(jsonObject);
            final PolicyConditionSet condition = getCondition((JSONObject) jsonObject.get("condition"));
            return new AuthorizationPolicy(identifier, priority, target, effect, condition);
        } catch (final Exception e) {
            throw new ProviderException(e);
        }
    }

    protected PolicyTarget getTarget(final JSONObject json) {
        return new PolicyTarget(
                json.get("subject").toString(),
                json.get("resource").toString(),
                json.get("action").toString());
    }

    protected AuthorizationPolicy.PolicyEffect getEffect(final JSONObject json) {
        final String effect = json.get("effect").toString();
        AuthorizationPolicy.PolicyEffect theEffect = AuthorizationPolicy.PolicyEffect.DENY;
        if (effect.equals("allow")) {
            theEffect = AuthorizationPolicy.PolicyEffect.ALLOW;
        }
        return theEffect;
    }

    public PolicyConditionSet getCondition(final JSONObject json) throws ProviderNotFoundException {

//        for (final Object key : json.keySet()) {
//            final Object value = json.get(key);
//            if (value instanceof JSONArray) {
//                final List<PolicyConditionSet> subConditions = processConditionSet((JSONArray) value);
//                return PolicyFactoryManager.getInstance().createPolicyConditionSet(key.toString(), subConditions);
//            } else {
//                return processCondition(json);
//            }
//        }
        if (json.keySet().size() > 1) {
            return processCondition(json);
        } else {
            for (final Object key : json.keySet()) {
                final Object value = json.get(key);
                if (value instanceof JSONArray) {
                    final List<PolicyConditionSet> subConditions = processConditionSet((JSONArray) value);
                    return PolicyFactoryManager.getInstance().createPolicyConditionSet(key.toString(), subConditions);
                }
            }
        }
        throw new IllegalArgumentException("Malformed policy condition");
    }

    protected PolicyCondition processCondition(final JSONObject json) throws ProviderNotFoundException {
        final PolicyFactoryManager mgr = PolicyFactoryManager.getInstance();
        String provider = "darc:condition";
        if (json.containsKey("provider")) {
            provider = json.get("provider").toString();
        }
        final HashMap<String, Object> conditionParts = new HashMap<>();
        conditionParts.putAll(json);

        if (json.containsKey("source")) {
            conditionParts.put("source", json.get("source").toString());
        }
        if (json.containsKey("value")) {
            conditionParts.put("value", json.get("value").toString());
        }
        if (json.containsKey("operation")) {
            final Object op = json.get("operation");
            final Map<String, String> arguments = new HashMap<>();
            String operationIdentifier = "";
            if (op instanceof JSONArray) {
                final JSONArray operation = (JSONArray) op;
                final Iterator<Object> it = operation.iterator();
                operationIdentifier = it.next().toString();
                while (it.hasNext()) {
                    final String[] split = it.next().toString().split("=");
                    if (split.length > 2) {
                        throw new IllegalArgumentException("Operation parameters cannot contain other equal-signs then the ones to separate argument-name and -value");
                    }
                    arguments.put(split[0], split[1]);
                }
            } else {
                operationIdentifier = op.toString();
            }
            conditionParts.put("operation", operationIdentifier);
            conditionParts.put("arguments", arguments);
        }
        List<String> theVerifiers = new ArrayList<>();
        if (json.containsKey("verifiers")) {
            final JSONArray verifiers = (JSONArray) json.get("verifiers");
            theVerifiers = new ArrayList<>(verifiers);
        }
        conditionParts.put("verifiers", theVerifiers);
        return mgr.createPolicyCondition(provider, conditionParts);
    }

    protected List<PolicyConditionSet> processConditionSet(final JSONArray jsonArray) throws ProviderNotFoundException {
        final List<PolicyConditionSet> conditions = new ArrayList<>();
        final Iterator<Object> it = jsonArray.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            if (obj instanceof JSONObject) {
                conditions.add(getCondition((JSONObject) obj));
            } else {
                throw new IllegalArgumentException("Malformed policy condition");
            }
        }
        return conditions;
    }
}
