package com.clanjhoo.vampire.json;

import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.entity.UPlayer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.UUID;

public class UPlayerDeserializer implements JsonDeserializer<UPlayer> {
    @Override
    public UPlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        UPlayer uplayer = null;
        if (jsonElement.isJsonObject()) {
            JsonObject root = jsonElement.getAsJsonObject();

            boolean isVampire = root.has("vampire") ? root.get("vampire").getAsBoolean() : false;
            boolean isNosferatu = root.has("nosferatu") ? root.get("nosferatu").getAsBoolean() : false;
            double infection = root.has("infection") ? root.get("infection").getAsDouble() : 0D;
            InfectionReason ir = root.has("reason") ? InfectionReason.fromName(root.get("reason").getAsString()) : null;
            UUID makerUUID = root.has("makerUUID") ? UUID.fromString(root.get("makerUUID").getAsString()) : null;
            boolean isIntending = root.has("intending") ? root.get("intending").getAsBoolean() : false;
            boolean hasNightvision = root.has("usingNightVision") ? root.get("usingNightVision").getAsBoolean() : false;

            uplayer = new UPlayer(isVampire, isNosferatu, infection, ir, makerUUID, isIntending, hasNightvision);
        }

        return uplayer;
    }
}
