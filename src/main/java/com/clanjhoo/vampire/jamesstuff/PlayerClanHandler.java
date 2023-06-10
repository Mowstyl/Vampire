package com.clanjhoo.vampire.jamesstuff;

public class PlayerClanHandler {

    private String uuid;
    private Clan clan;
    private boolean turnIntent;

    public PlayerClanHandler(String uuid, Clan clan) {
        this.uuid = uuid;
        this.clan = clan;
        turnIntent = false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public boolean isTurnIntent() {
        return turnIntent;
    }

    public void setTurnIntent(boolean turnIntent) {
        this.turnIntent = turnIntent;
    }
}
