package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;

import java.util.logging.Level;

public class ProtocolLibCompat {
    private final ProtocolManager protocolManager;
    private final VampireRevamp plugin;

    public ProtocolLibCompat(VampireRevamp plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();

        plugin.log(Level.INFO, "ProtocolLib hooks enabled.");
    }

    public void addPacketListener(PacketListener listener) {
        protocolManager.addPacketListener(listener);
    }

    public void removePacketListener(PacketListener listener) {
        protocolManager.removePacketListener(listener);
    }
}
