package com.clanjhoo.vampire.event;

import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InfectionChangeEvent extends Event {
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

    private double infection;

    public double getInfection() {
        return this.infection;
    }

    public void setInfection(double infection) {
        this.infection = infection;
    }

    private final VPlayer uplayer;

    public VPlayer getUplayer() {
        return this.uplayer;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public InfectionChangeEvent(double infection, VPlayer uplayer) {
        this.infection = infection;
        this.uplayer = uplayer;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancelled() {
        return this.cancel;
    }
}
