package com.zapp.library.merchant.network.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * The base custom deserializer from json to java objects. Contains common helper methods.
 */
public abstract class BaseDeserializer {
    /**
     * A helper method which returns null in case such element name doesn't exist in the given json object.
     *
     * @param jsonObject the json object which contains the element with such name.
     * @param name       name of the member that is being requested.
     * @return the string value of member matching the name. Null if no such member exists.
     */
    protected String getStringElement(final JsonObject jsonObject, final String name) {
        final JsonElement element = jsonObject.get(name);
        String value = null;
        if (element != null) {
            value = element.getAsString();
        }
        return value;
    }
}
