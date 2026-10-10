package com.clanjhoo.vampire.keyproviders;


public enum InfectionMessageKeys implements KeyProvider {
    ALTAR("infection.altar"),
    COMBAT_MISTAKE("infection.combatMistake"),
    COMBAT_INTENDED("infection.combatIntended"),
    TRADE("infection.trade"),
    FLASK("infection.flask"),
    COMMAND("infection.command"),
    UNKNOWN("infection.unknown"),
    CURED("infection.cured"),
    FEELING("infection.feeling"),
    HINT("infection.hint")
    ;

    InfectionMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
}
