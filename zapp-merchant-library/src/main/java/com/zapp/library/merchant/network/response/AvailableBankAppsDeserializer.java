package com.zapp.library.merchant.network.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom deserializer for {@link AvailableBankAppsResponse}
 */

public class AvailableBankAppsDeserializer extends BaseDeserializer implements JsonDeserializer<List<AvailableBankAppsResponse>> {

    @Override
    public List<AvailableBankAppsResponse> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonArray jsonArray = json.getAsJsonArray();
        final List<AvailableBankAppsResponse> availableBankAppsRepons = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final AvailableBankAppsResponse availableBank = new AvailableBankAppsResponse();
            availableBank.setBankName(getStringElement(jsonObject, "name"));
            availableBank.setSmallLogo(getStringElement(jsonObject, "shortLogo"));
            availableBank.setLargeLogo(getStringElement(jsonObject, "longLogo"));
            availableBankAppsRepons.add(availableBank);
        }
        return availableBankAppsRepons;
    }
}
