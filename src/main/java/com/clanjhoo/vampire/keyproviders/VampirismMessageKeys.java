package com.clanjhoo.vampire.keyproviders;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum VampirismMessageKeys implements MessageKeyProvider {
    ALREADY_VAMPIRE("vampirism.alreadyVampire"),
    TURNED_VAMPIRE("vampirism.turnedVampire"),
    CURED_VAMPIRE("vampirism.curedVampire"),
    CANT_EAT_ITEM("vampirism.cantEatItem"),
    TRUCE_BROKEN("vampirism.truceBroken"),
    TRUCE_RESTORED("vampirism.truceRestored"),
    COMBAT_WOOD_WARNING("vampirism.combatWoodWarning")
    ;

    VampirismMessageKeys(String key) {
        this.key = MessageKey.of(key);
    }

    private final MessageKey key;

    @Override
    public MessageKey getMessageKey() {
        return key;
    }
}
