package com.clanjhoo.vampire.entity;

import com.clanjhoo.vampire.VampireRevamp;
import com.google.gson.Gson;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class UPlayerColl {
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //

	private static final Map<UUID, UPlayer> offlinePlayers = new ConcurrentHashMap<>();
	private static final Map<UUID, UPlayer> onlinePlayers = new ConcurrentHashMap<>();

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	@Nonnull
	private static UPlayer load(@Nonnull UUID uuid) {
		UPlayer uPlayer = null;
		boolean loaded = false;

		try {
			File playerFile = new File(VampireRevamp.getInstance().getPlayerFolder(), uuid.toString() + ".json");

			if (playerFile.exists()) {
				Gson gson = VampireRevamp.getInstance().gson;
				Reader reader = new FileReader(playerFile);
				uPlayer = gson.fromJson(reader, UPlayer.class);
				reader.close();
				loaded = true;
			}
		}
		catch (Exception ignore) {}

		if (!loaded) {
			uPlayer = new UPlayer(false, false, 0D, null,
					null, false, false);
		}
		uPlayer.setPlayerUUID(uuid);

		return uPlayer;
	}


	@Nonnull
	public static UPlayer get(@Nonnull UUID uuid) {
		UPlayer uPlayer = null;

		if (onlinePlayers.containsKey(uuid)) {
			uPlayer = onlinePlayers.get(uuid);
		}
		else if (offlinePlayers.containsKey(uuid)) {
			uPlayer = offlinePlayers.get(uuid);
		}
		else {
			uPlayer = load(uuid);
			offlinePlayers.put(uuid, uPlayer);
		}

		return uPlayer;
	}

	public static boolean savePlayerData(@Nonnull UPlayer uPlayer) {
		boolean result = false;
		File playerFolder = VampireRevamp.getInstance().getPlayerFolder();

		try {
			if (!playerFolder.exists()) {
				playerFolder.mkdir();
			}

			File playerFile = new File(playerFolder, uPlayer.getPlayerUUID().toString() + ".json");
			if (!uPlayer.isHealthy()) {
				Gson gson = VampireRevamp.getInstance().gson;

				if (!playerFile.exists()) {
					playerFile.createNewFile();
				}

				Writer writer = new FileWriter(playerFile);
				gson.toJson(uPlayer, writer);
				writer.flush();
				writer.close();
				result = true;
			}
			else {
				if (playerFile.exists()) {
					result = playerFile.delete();
				}
				else {
					result = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public static Collection<UPlayer> getAllOnlineInfected() {
		Collection<UPlayer> infectedPlayers = new HashSet<>();

		for (UPlayer uPlayer : onlinePlayers.values()) {
			if (uPlayer.isInfected()) {
				infectedPlayers.add(uPlayer);
			}
		}

		return infectedPlayers;
	}
	
	public static Collection<UPlayer> getAllOnlineVampires() {
		Collection<UPlayer> vampirePlayers = new HashSet<>();

		for (UPlayer uPlayer : onlinePlayers.values()) {
			if (uPlayer.isVampire()) {
				vampirePlayers.add(uPlayer);
			}
		}

		return vampirePlayers;
	}

	public static Collection<UPlayer> getOnlinePlayers() {
		return onlinePlayers.values();
	}

	public static void toOfflinePlayer(@Nonnull UUID uuid) {
		UPlayer uPlayer = null;
		if (onlinePlayers.containsKey(uuid)) {
			uPlayer = onlinePlayers.get(uuid);
			onlinePlayers.remove(uuid);
		}
		if (uPlayer == null) {
			uPlayer = get(uuid);
		}
		uPlayer.setPlayer(null);
		offlinePlayers.put(uuid, onlinePlayers.get(uuid));
	}

	public static void toOnlinePlayer(@Nonnull UUID uuid, @Nonnull Player player) {
		UPlayer uPlayer = null;
		if (offlinePlayers.containsKey(uuid)) {
			uPlayer = offlinePlayers.get(uuid);
			offlinePlayers.remove(uuid);
		}
		if (uPlayer == null) {
			uPlayer = get(uuid);
		}
		uPlayer.setPlayer(player);
		onlinePlayers.put(uuid, uPlayer);
	}

	public static void clearOfflinePlayers() {
		offlinePlayers.entrySet().removeIf(entry -> UPlayerColl.savePlayerData(entry.getValue()));
	}

	private static void clearOnlinePlayers() {
		onlinePlayers.entrySet().removeIf(entry -> UPlayerColl.savePlayerData(entry.getValue()));
	}

	public static void saveAllPlayers() {
		clearOnlinePlayers();
		clearOfflinePlayers();
		if (onlinePlayers.size() != 0 || offlinePlayers.size() != 0) {
			VampireRevamp.log(Level.SEVERE, "Some users could not be saved!");
		}
	}
}
