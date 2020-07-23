package com.clanjhoo.vampire.event;

import com.clanjhoo.vampire.entity.UPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VampireTypeChangeEvent extends Event {
    // -------------------------------------------- //
    // REQUIRED EVENT CODE
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // -------------------------------------------- //
    // FIELD
    // -------------------------------------------- //

    private boolean vampire;

    public boolean isVampire() {
        return this.vampire;
    }

    public void setVampire(boolean vampire) {
        this.vampire = vampire;
    }

    private final UPlayer uplayer;

    public UPlayer getUplayer() {
        return this.uplayer;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public VampireTypeChangeEvent(boolean vampire, UPlayer uplayer) {
        this.vampire = vampire;
        this.uplayer = uplayer;
        this.cancel = false;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancelled() {
        return this.cancel;
    }
}
