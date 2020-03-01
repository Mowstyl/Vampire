package com.clanjhoo.vampire.json;

import com.clanjhoo.vampire.entity.UPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UPlayerSerializer implements JsonSerializer<UPlayer> {
    @Override
    public JsonElement serialize(UPlayer uPlayer, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();

        root.addProperty("vampire", uPlayer.isVampire());
        root.addProperty("nosferatu", uPlayer.isNosferatu());
        root.addProperty("infection", uPlayer.getInfection());
        if (uPlayer.getReason() != null)
            root.addProperty("reason", uPlayer.getReason().name());
        if (uPlayer.getMakerUUID() != null)
            root.addProperty("makerUUID", uPlayer.getMakerUUID().toString());
        root.addProperty("intending", uPlayer.isIntending());
        root.addProperty("usingNightVision", uPlayer.isUsingNightVision());

        return root;
    }
}
