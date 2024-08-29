package com.clanjhoo.vampire.event;

import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class InfectionChangeEvent extends Event {
    // -------------------------------------------- //
    // REQUIRED EVENT CODE
    // -------------------------------------------- //

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;

    @Override
    public @NotNull HandlerList getHandlers() {
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

    private final VPlayer vPlayer;

    public VPlayer getVPlayer() {
        return this.vPlayer;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public InfectionChangeEvent(double infection, VPlayer uplayer) {
        this.infection = infection;
        this.vPlayer = uplayer;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancelled() {
        return this.cancel;
    }
}
