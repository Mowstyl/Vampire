package com.clanjhoo.vampire;

import co.aikar.commands.PaperCommandManager;
import com.clanjhoo.vampire.Listeners.ListenerMain;
import com.clanjhoo.vampire.Listeners.PhantomListener;
import com.clanjhoo.vampire.cmd.CmdVampire;
import com.clanjhoo.vampire.entity.MConf;
import com.clanjhoo.vampire.entity.MLang;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.entity.UPlayerColl;
import com.clanjhoo.vampire.json.MConfDeserializer;
import com.clanjhoo.vampire.json.MConfSerializer;

import com.clanjhoo.vampire.json.UPlayerDeserializer;
import com.clanjhoo.vampire.json.UPlayerSerializer;
import com.clanjhoo.vampire.tasks.BatTask;
import com.clanjhoo.vampire.tasks.TheTask;
import com.clanjhoo.vampire.util.DisguiseUtil;
import com.clanjhoo.vampire.util.EntityUtil;
import com.clanjhoo.vampire.util.ResourceUtil;
import com.clanjhoo.vampire.util.SunUtil;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.GsonBuilder;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class VampireRevamp extends JavaPlugin {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private int theTaskId = -1;
	private int batTaskId = -1;
	private GsonBuilder gsonb;
	public Gson gson;
	public PaperCommandManager pumanager;
	public Map<UUID, Boolean> batEnabled = new ConcurrentHashMap<>();
	public Set<LivingEntity> bats = new HashSet<>();
	public Map<UUID, List<LivingEntity>> batmap = new ConcurrentHashMap<>();
	public boolean isDisguiseEnabled = false;
	public MConf mConf;
	public MLang mLang;
	public UPlayerColl uPlayerColl;
	public static boolean isPapermc = false;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	@Override
	public void onLoad() {
		isPapermc = false;
		try {
			isPapermc = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
		} catch (ClassNotFoundException e) {
			this.getLogger().info("Use Paper for more features like Phantom related ones!");
		}

		if (isPapermc) {
			this.getLogger().info("Using Paper");
		}

		// Create Gson
		gsonb = new GsonBuilder();
		gsonb.registerTypeAdapter(MConf.class, new MConfSerializer());
		gsonb.registerTypeAdapter(MConf.class, new MConfDeserializer(this));
		gsonb.registerTypeAdapter(UPlayer.class, new UPlayerSerializer());
		gsonb.registerTypeAdapter(UPlayer.class, new UPlayerDeserializer());
		gson = gsonb.setPrettyPrinting().create();

		File dataFolder = this.getDataFolder();
		try {
			if (!dataFolder.exists()) {
				dataFolder.mkdir();
			}

			ResourceUtil.plugin = this;
			EntityUtil.plugin = this;
			SunUtil.plugin = this;
			UPlayer.plugin = this;
			this.mLang = MLang.load(this);
			this.mConf = MConf.load(this);
			this.uPlayerColl = new UPlayerColl(this);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onEnable()
	{
		isDisguiseEnabled = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");
		if (isDisguiseEnabled)
			DisguiseUtil.plugin = this;
		pumanager = new PaperCommandManager(this);

		pumanager.enableUnstableAPI("help");
		pumanager.registerCommand(new CmdVampire(this));

		pumanager.getCommandCompletions().registerAsyncCompletion("yesno", c -> ImmutableList.of("yes", "no"));

		// Listener
		Bukkit.getPluginManager().registerEvents(new ListenerMain(this), this);
		if (isPapermc && this.mConf.getTruceEntityTypes().contains(EntityType.PHANTOM))
			Bukkit.getPluginManager().registerEvents(new PhantomListener(this), this);

		BukkitScheduler scheduler = getServer().getScheduler();

		theTaskId = scheduler.scheduleSyncRepeatingTask(this, new TheTask(), 0L, (mConf.taskDelayMillis * 20) / 1000);
		batTaskId = scheduler.scheduleSyncRepeatingTask(this, new BatTask(this), 0L, (mConf.taskDelayMillis * 20) / 1000);
	}

	public static void log(String message) {
		Bukkit.getLogger().log(Level.INFO, message);
	}

	public File getPlayerFolder() {
		return new File(this.getDataFolder(), "store");
	}

	@Override
	public void onDisable() {
		this.getLogger().log(Level.INFO, "Saving player data...");
		uPlayerColl.saveAll();
		this.getLogger().log(Level.INFO, "Saved!");
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.cancelTask(theTaskId);
		scheduler.cancelTask(batTaskId);
	}
}