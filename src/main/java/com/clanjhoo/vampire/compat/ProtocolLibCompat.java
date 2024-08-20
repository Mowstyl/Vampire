package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.VampireRevamp;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;

import java.util.logging.Level;

public class ProtocolLibCompat {
    private ProtocolManager protocolManager = null;

    public ProtocolLibCompat() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        VampireRevamp.log(Level.INFO, "ProtocolLib hooks enabled.");
    }

    public void addPacketListener(PacketListener listener) {
        protocolManager.addPacketListener(listener);
    }

    public void removePacketListener(PacketListener listener) {
        protocolManager.removePacketListener(listener);
    }
}
