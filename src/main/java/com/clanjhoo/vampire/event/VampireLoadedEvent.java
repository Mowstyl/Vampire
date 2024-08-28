package com.clanjhoo.vampire.event;

import com.clanjhoo.dbhandler.events.LoadedDataEvent;
import com.clanjhoo.vampire.entity.VPlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public class VampireLoadedEvent extends LoadedDataEvent<VPlayer> {

    private static final HandlerList handlers = new HandlerList();


    public VampireLoadedEvent(@NotNull List<Serializable> keys, @Nullable VPlayer data, @Nullable Exception exception) {
        super(keys, data, exception);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
