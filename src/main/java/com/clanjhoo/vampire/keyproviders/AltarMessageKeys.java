package com.clanjhoo.vampire.keyproviders;


public enum AltarMessageKeys implements KeyProvider {
    INCOMPLETE("altars.incomplete"),
    RESOURCE("altars.resource"),
    ACTIVATE_SUCCESS("altars.activateSuccess"),
    ACTIVATE_FAIL("altars.activateFail"),
    ALTAR_DARK_NAME("altars.altarDarkName"),
    ALTAR_DARK_DESC("altars.altarDarkDesc"),
    ALTAR_DARK_COMMON("altars.altarDarkCommon"),
    ALTAR_DARK_VAMPIRE("altars.altarDarkVampire"),
    ALTAR_DARK_INFECTED("altars.altarDarkInfected"),
    ALTAR_DARK_HEALTHY("altars.altarDarkHealthy"),
    ALTAR_LIGHT_NAME("altars.altarLightName"),
    ALTAR_LIGHT_DESC("altars.altarLightDesc"),
    ALTAR_LIGHT_COMMON("altars.altarLightCommon"),
    ALTAR_LIGHT_VAMPIRE("altars.altarLightVampire"),
    ALTAR_LIGHT_INFECTED("altars.altarLightInfected"),
    ALTAR_LIGHT_HEALTHY("altars.altarLightHealthy")
    ;

    AltarMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
}
