package com.clanjhoo.vampire.keyproviders;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum SkillMessageKeys implements MessageKeyProvider {
    BLOODLUST_LOW_FOOD("skills.bloodlust_lowFood"),
    BLOODLUST_GAMEMODE_CHECK("skills.bloodlust_gamemodeCheck"),
    FLASK_INSUFFICIENT("skills.flask_insufficient"),
    FLASK_BLOODLUSTING("skills.flask_bloodlusting"),
    FLASK_SUCCESS("skills.flask_success"),
    SHRIEK_WAIT("skills.shriek_wait")
    ;

    SkillMessageKeys(String key) {
        this.key = MessageKey.of(key);
    }

    private final MessageKey key;

    @Override
    public MessageKey getMessageKey() {
        return key;
    }
}
