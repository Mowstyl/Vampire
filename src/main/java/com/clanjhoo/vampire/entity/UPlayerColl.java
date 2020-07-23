package com.clanjhoo.vampire.entity;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.FileUtil;
import com.google.common.io.Files;
import com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UPlayerColl {
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //

	private final Map<UUID, UPlayer> translator;
	private final VampireRevamp plugin;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	public UPlayerColl(VampireRevamp plugin) {
		this.plugin = plugin;
		this.translator = loader();
	}

	private Map<UUID, UPlayer> loader() {
		Map<UUID, UPlayer> UUIDUPlayer = new ConcurrentHashMap<>();
		List<File> playerFiles = null;

		try {
			File playerFolder = this.plugin.getPlayerFolder();

			if (playerFolder.exists()) {
				playerFiles = FileUtil.searchJson(playerFolder);
			}
			else {
				playerFolder.mkdir();
			}
		}
		catch (Exception ignore) {}

		if (playerFiles != null && playerFiles.size() > 0) {
			Gson gson = this.plugin.gson;
			for (File playerFile : playerFiles) {
				try {
					UUID playerUUID = UUID.fromString(Files.getNameWithoutExtension(playerFile.getName()));
					Reader reader = new FileReader(playerFile);
					//Bukkit.getLogger().log(Level.INFO, "Reading player " + playerUUID.toString());
					UPlayer uPlayer = gson.fromJson(reader, UPlayer.class);
					reader.close();
					uPlayer.setPlayerUUID(playerUUID);
					UUIDUPlayer.put(playerUUID, uPlayer);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return UUIDUPlayer;
	}

	public void savePlayer(UUID playerUUID) {
		File playerFolder = this.plugin.getPlayerFolder();

		try {
			if (!playerFolder.exists()) {
				playerFolder.mkdir();
			}

			File playerFile = new File(playerFolder, playerUUID.toString() + ".json");
			if (!this.translator.get(playerUUID).isHealthy()) {
				Gson gson = this.plugin.gson;

				if (!playerFile.exists()) {
					playerFile.createNewFile();
				}

				Writer writer = new FileWriter(playerFile);
				gson.toJson(this.translator.get(playerUUID), writer);
				writer.flush();
				writer.close();
			}
			else {
				if (playerFile.exists()) {
					playerFile.delete();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveAll() {
		File playerFolder = this.plugin.getPlayerFolder();

		try {
			if (!playerFolder.exists()) {
				playerFolder.mkdir();
			}

			Gson gson = this.plugin.gson;

			for (UUID playerUUID : this.translator.keySet()) {
				File playerFile = new File(playerFolder, playerUUID.toString() + ".json");
				if (!this.translator.get(playerUUID).isHealthy()) {
					if (!playerFile.exists()) {
						playerFile.createNewFile();
					}

					Writer writer = new FileWriter(playerFile);
					gson.toJson(this.translator.get(playerUUID), writer);
					writer.flush();
					writer.close();
				}
				else {
					if (playerFile.exists()) {
						playerFile.delete();
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //


	public Collection<UPlayer> getAll()
	{
		return this.translator.values();
	}
	
	public Collection<UPlayer> getAllOnlineInfected()
	{
		Collection<UPlayer> infectedPlayers = new HashSet<>();

		for (UPlayer uPlayer : this.translator.values()) {
			if (uPlayer.getPlayer().isOnline() && uPlayer.isInfected()) {
				infectedPlayers.add(uPlayer);
			}
		}

		return infectedPlayers;
	}
	
	public Collection<UPlayer> getAllOnlineVampires()
	{
		Collection<UPlayer> vampirePlayers = new HashSet<>();

		for (UPlayer uPlayer : this.translator.values()) {
			if (uPlayer.getPlayer().isOnline() && uPlayer.isVampire()) {
				vampirePlayers.add(uPlayer);
			}
		}

		return vampirePlayers;
	}

	public UPlayer get(UUID uuid) {
		return this.translator.get(uuid);
	}

	public UPlayer add(Player player) {
		UPlayer uPlayer = new UPlayer(false, false, 0D, null,
									null, false, false);
		uPlayer.setPlayer(player);
		this.translator.put(player.getUniqueId(), uPlayer);

		return uPlayer;
	}
}
