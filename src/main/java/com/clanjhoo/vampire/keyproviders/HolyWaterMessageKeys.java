package com.clanjhoo.vampire.keyproviders;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum HolyWaterMessageKeys implements MessageKeyProvider {
    CREATE_SUCCESS("holyWater.createSuccess"),
    CREATE_FAIL("holyWater.createFail"),
    CREATE_RESULT("holyWater.createResult"),
    COMMON_REACT("holyWater.commonReact"),
    VAMPIRE_REACT("holyWater.vampireReact"),
    INFECTED_REACT("holyWater.infectedReact"),
    HEALTHY_REACT("holyWater.healthyReact")
    ;

    HolyWaterMessageKeys(String key) {
        this.key = MessageKey.of(key);
    }

    private final MessageKey key;

    @Override
    public MessageKey getMessageKey() {
        return key;
    }
}
