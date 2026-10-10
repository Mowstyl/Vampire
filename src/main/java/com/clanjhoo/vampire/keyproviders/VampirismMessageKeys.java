package com.clanjhoo.vampire.keyproviders;


public enum VampirismMessageKeys implements KeyProvider {
    ALREADY_VAMPIRE("vampirism.alreadyVampire"),
    TURNED_VAMPIRE("vampirism.turnedVampire"),
    CURED_VAMPIRE("vampirism.curedVampire"),
    CANT_EAT_ITEM("vampirism.cantEatItem"),
    TRUCE_BROKEN("vampirism.truceBroken"),
    TRUCE_RESTORED("vampirism.truceRestored"),
    COMBAT_WOOD_WARNING("vampirism.combatWoodWarning"),
    CANT_SLEEP("vampirism.cantSleep")
    ;

    VampirismMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
}
