package com.clanjhoo.vampire.keyproviders;


public enum GrammarMessageKeys implements KeyProvider {
    PLAYER("grammar.player"),
    YOU("grammar.you"),
    TO_BE_2ND("grammar.toBeSecond"),
    TO_BE_3RD("grammar.toBeThird"),
    TO_BE_2ND_PAST("grammar.toBeSecondPast"),
    TO_BE_3RD_PAST("grammar.toBeThirdPast"),
    BLOODLUST("grammar.bloodlust"),
    NIGHTVISION("grammar.nightvision"),
    SHRIEK("grammar.shriek"),
    INTEND("grammar.intend"),
    BATUSI("grammar.batusi"),
    X_IS_Y("grammar.xIsY"),
    COMBAT_DAMAGE("grammar.combatDamage"),
    HUMAN_TYPE("grammar.humanType"),
    INFECTED_TYPE("grammar.infectedType"),
    VAMPIRE_TYPE("grammar.vampireType"),
    NOSFERATU_TYPE("grammar.nosferatuType"),
    ON("grammar.enabled"),
    OFF("grammar.disabled"),
    ONLINE("grammar.online"),
    OFFLINE("grammar.offline"),
    ONLY_TYPE_CAN_ACTION("grammar.onlyTypeCanAction")
    ;

    GrammarMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
}
