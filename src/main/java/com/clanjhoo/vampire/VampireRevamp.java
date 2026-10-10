package com.clanjhoo.vampire;

import com.clanjhoo.dbhandler.data.DBObjectManager;
import com.clanjhoo.dbhandler.data.SaveOperation;
import com.clanjhoo.dbhandler.data.StorageType;
import com.clanjhoo.vampire.cmd.MessageType;
import com.clanjhoo.vampire.compat.*;
import com.clanjhoo.vampire.config.StorageConfig;
import com.clanjhoo.vampire.event.VampireLoadedEvent;
import com.clanjhoo.vampire.keyproviders.InfectionMessageKeys;
import com.clanjhoo.vampire.keyproviders.KeyProvider;
import com.clanjhoo.vampire.listeners.*;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import com.clanjhoo.vampire.altar.AltarDark;
import com.clanjhoo.vampire.altar.AltarLight;
import com.clanjhoo.vampire.cmd.CmdVampire;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;

import com.clanjhoo.vampire.tasks.BatTask;
import com.clanjhoo.vampire.tasks.RaytracingTask;
import com.clanjhoo.vampire.tasks.TheTask;
import com.clanjhoo.vampire.util.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VampireRevamp extends JavaPlugin {

	private final ConcurrentHashMap<UUID, VPlayer> playerCollection = new ConcurrentHashMap<>();
	private boolean isPapermc = false;
	private Permission perms = null;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private BukkitTask cleanTask = null;
	private BukkitTask theTask = null;
	private BukkitTask batTask = null;
	private BukkitTask rayTraceTask = null;
	private RaytracingTask rayTraceTaskInstance = null;
	public final Map<Locale, Map<String, String>> locales = new HashMap<>();
	public final Map<Locale, Tuple<Integer, Integer>> maxFeelHint = new HashMap<>();
	public final Map<UUID, Boolean> batEnabled = new ConcurrentHashMap<>();
	public final Set<LivingEntity> bats = new HashSet<>();
	public final Map<UUID, List<LivingEntity>> batmap = new ConcurrentHashMap<>();
	private boolean isDisguiseEnabled = false;
	private boolean hasVault = false;
	private PluginConfig conf = null;
	private AltarDark altarDark;
	private AltarLight altarLight;
	private SemVer serverVersion;
	private WorldGuardCompat wg = null;
	private ProtocolLibCompat plc = null;
	private WerewolfCompat ww;
	private VampireExpansion expansionPAPI;
	private DisguiseListener dl;
	private PhantomListener pl;
	private EntryVampiresListener dvl;
	private BedListener bl;
	private VersionCompat vc;

	private DBObjectManager<VPlayer> vPlayerManager;
	private CommandDispatcher<CommandSourceStack> dispatcher;


	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //


	public CommandDispatcher<CommandSourceStack> getDispatcher() {
		return dispatcher;
	}

	public boolean isDisguiseEnabled() {
		return isDisguiseEnabled;
	}

	public VersionCompat getVersionCompat() {
		return vc;
	}

	public boolean setVampireGroup(Player player, boolean isVampire) {
		if (perms != null) {
			if (isVampire)
				return perms.playerAddGroup(player, conf.compatibility.vampirePermissionGroup);
			else
				return perms.playerRemoveGroup(player, conf.compatibility.vampirePermissionGroup);
		}
		return false;
	}

	public boolean permissionGroupEnabled() {
		return perms != null;
	}

	public boolean isPaperMc() {
		return isPapermc;
	}

	public AltarLight getAltarLight() {
		return altarLight;
	}

	public AltarDark getAltarDark() {
		return altarDark;
	}

	public SemVer getServerVersion() {
		return serverVersion;
	}

	public boolean isWorldGuardEnabled() {
		return wg != null && getVampireConfig().compatibility.useWorldGuardRegions;
	}

	public WorldGuardCompat getWorldGuardCompat() {
		return wg;
	}

	public WerewolfCompat getWerewolvesCompat() {
		return ww;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	@Override
	public void onLoad() {
		isPapermc = false;
		try {
			Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData");
			isPapermc = true;
		} catch (ClassNotFoundException e) {
			log(Level.INFO, "Use Paper for more features like Phantom and Bed related ones!");
		}

		if (isPapermc) {
			log(Level.INFO, "Using Paper");
		}

		String versionString = this.getServer().getVersion();
		try {
			String versionRegex = "^(?:.*MC: )?([0-9]+)\\.([0-9]+)(?:\\.([0-9]+))?(?:.*)?$";
			Pattern versionPattern = Pattern.compile(versionRegex);

			Matcher versionMatcher = versionPattern.matcher(versionString);
			if (versionMatcher.find()) {
				//log(Level.INFO, "Found " + versionMatcher.groupCount() + " groups!");
				int major = Integer.parseInt(versionMatcher.group(1));
				int minor = Integer.parseInt(versionMatcher.group(2));
				int patch = 0;
				try {
					patch = Integer.parseInt(versionMatcher.group(3));
				}
				catch (NumberFormatException ignored) {}
				serverVersion = new SemVer(major, minor, patch);
				log(Level.INFO, "Detected server version: " + serverVersion);
				log(Level.INFO, "If this is not the version you are running please contact the plugin developer.");
			}
			else {
				throw new IllegalStateException("No match found after find");
			}
		}
		catch (Exception ex) {
			log(Level.SEVERE, "Error found while detecting server version. Version: " + versionString);
			ex.printStackTrace();
			setEnabled(false);
			return;
		}

		try {
			File localesFolder = new File(this.getDataFolder() + "/locales");
			if (!localesFolder.exists()) {
				if (!localesFolder.mkdirs()) {
					log(Level.SEVERE, "The locales folder could not be created! Disabling plugin!");
					setEnabled(false);
					return;
				}
			}
			String[] providedLocales = new String[]{"lang_en.yml", "lang_es.yml"};
			for (String locale : providedLocales) {
				File localeFile = new File(localesFolder, locale);
				if (!localeFile.exists()) {
					try (InputStream is = getResource("locales/" + locale)) {
						if (is != null) {
							Files.copy(is, localeFile.toPath()); //Copies file from plugin jar into newly created file.
						}
					}
				}
			}
		}
		catch (Exception ex) {
			log(Level.WARNING, "Error found while creating default locale files.");
			ex.printStackTrace();
		}

		try {
			vc = new VersionCompat(this, serverVersion);
			// Test versionCompat is working
			vc.test();
		}
		catch (Exception ex) {
			log(Level.SEVERE, "Error found while loading methods for minecraft version: " + serverVersion);
			ex.printStackTrace();
			setEnabled(false);
			return;
		}


		try {
			this.saveDefaultConfig();
		}
		catch (Exception ex) {
			log(Level.WARNING, "Error found while saving default config.yml!");
			ex.printStackTrace();
		}

		vPlayerManager = null;
		loadConfig(true);
		loadLocales(false);

		// WorldGuard compat
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
			wg = new WorldGuardCompat(this);
		else
			log(Level.WARNING, "WorldGuard plugin not detected. Disabled WorldGuard compat.");
	}

	@Override
	public void saveDefaultConfig() {
		File dataFolder = this.getDataFolder();
		File configFile = new File(dataFolder, "config.yml");
		if (!dataFolder.exists()) {
			if (!dataFolder.mkdirs()) {
				log(Level.SEVERE, "The plugin folder could not be created! Disabling plugin!");
				setEnabled(false);
				return;
			}
		}

		if (!configFile.exists()) {
			log(Level.INFO, "Generating default config.yml...");
			PluginConfig conf = new PluginConfig(this);
			conf.saveConfigToFile(configFile);
			log(Level.INFO, "Done!");
		}
	}

	public boolean reloadVampireConfig() {
		this.reloadConfig();
		ww.disable();
		HandlerList.unregisterAll(pl);
		HandlerList.unregisterAll(dvl);
		boolean result = loadConfig(false);

		loadCompat();
		return result;
	}

	public void loadCompat() {
		// Werewolves compat
		ww = new WerewolfCompat(this);
		if (pl != null) {
			HandlerList.unregisterAll(pl);
			pl = null;
		}
		if (dvl != null) {
			HandlerList.unregisterAll(dvl);
			dvl = null;
		}
		if (dl != null) {
			HandlerList.unregisterAll(dl);
			dl = null;
		}
		if (bl != null) {
			HandlerList.unregisterAll(bl);
			plc.removePacketListener(bl.leaveButtonListener);
			bl = null;
		}

		// Paper compat
		if (isPapermc && new SemVer(1, 13).compareTo(serverVersion) < 0) {
			pl = new PhantomListener(this);
			Bukkit.getPluginManager().registerEvents(pl, this);
		}

		if (wg != null && wg.usingWG()) {
			dvl = new EntryVampiresListener(this);
			Bukkit.getPluginManager().registerEvents(dvl, this);
		}

		if (isDisguiseEnabled) {
			dl = new DisguiseListener(this);
			Bukkit.getPluginManager().registerEvents(dl, this);
		}

		if (conf.vampire.canSleepDaytime) {
			if (plc != null) {
				if (isPapermc) {
					bl = new BedListener(this);
					Bukkit.getPluginManager().registerEvents(bl, this);
					plc.addPacketListener(bl.leaveButtonListener);
				}
				else {
					log(Level.WARNING, "You need to be running a Paper server to enable sleep during daytime");
				}
			}
			else {
				log(Level.WARNING, "You need the ProtocolLib plugin to enable sleep during daytime");
			}
		}
	}

	public boolean reloadLocales() {
		return loadLocales(false);
	}

	public boolean reloadAll() {
		return reloadVampireConfig() && reloadLocales();
	}

	private boolean loadConfig(boolean loadDefaults) {
		boolean result = false;
		try {
			this.conf = new PluginConfig(this, this.getConfig());
			log(Level.INFO, "Loaded configuration!");
			debugLog(Level.INFO, this.conf.toString());
			result = true;
		}
		catch (Exception ex) {
			log(Level.WARNING, "Error found while loading config.yml!");
			ex.printStackTrace();
			if (loadDefaults) {
				log(Level.INFO, "Loading default config...");
				this.conf = new PluginConfig(this);
			}
		}
		boolean reloadPlayers = false;

		if (vPlayerManager != null) {
			reloadPlayers = true;
			log(Level.INFO, "Saving player data...");
			vPlayerManager.saveAll(SaveOperation.SAVE_ALL);
			log(Level.INFO, "Saved!");
		}
		if (vPlayerManager == null) {
			try {
				StorageConfig sconf = conf.storage;
				StorageType type;
				Object[] data;
				if (conf.storage.storageType == StorageType.JSON) {
					type = StorageType.JSON;
					data = new Object[]{"store"};
				} else {
					type = StorageType.MARIADB;
					data = new Object[]{
							sconf.address,
							sconf.port,
							sconf.database,
							sconf.username,
							sconf.password,
							sconf.prefix};
				}
				vPlayerManager = new DBObjectManager<>(
						VPlayer.class,
						this,
						type,
						VampireLoadedEvent::new,
						(vPlayer) -> !vPlayer.isHealthy(),
						5 * 60 * 1000,
						data);
				vPlayerManager.initialize();
			}
			catch (IOException ex) {
				log(Level.SEVERE, "Could not instantiate VPlayer manager! Disabling plugin!");
				ex.printStackTrace();
				setEnabled(false);
				return false;
			}
		}
		if (reloadPlayers) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				loadVPlayerFromDB(p.getUniqueId());
			}
		}
		if (rayTraceTaskInstance != null)
			rayTraceTaskInstance.stop();
		rayTraceTaskInstance = null;
		if (rayTraceTask != null)
			rayTraceTask.cancel();
		rayTraceTask = null;
		if (this.isEnabled() && !conf.radiation.useOldRadiationFormula) {
			rayTraceTaskInstance = new RaytracingTask(this);
			rayTraceTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, rayTraceTaskInstance, 50L, 50L);
		}

		return result;
	}

	private boolean loadLocales(boolean disableOnFail) {
		boolean hasDefaultLocale = false;
		boolean finalResult = false;
		try {
			File folder = new File(this.getDataFolder() + "/locales");
			File[] files = folder.listFiles();
			locales.clear();
			if (files != null) {
				for (final File file : files) {
					if (file.isFile()) {
						String[] raw = file.getName().split("\\.");
						if (raw.length != 2 || (!raw[1].equalsIgnoreCase("yml") && !raw[1].equalsIgnoreCase("yaml")))
							continue;
						raw = raw[0].split("_");
						if (!raw[0].equalsIgnoreCase("lang"))
							continue;
						if (raw.length > 3 || raw.length < 2) {
							log(Level.WARNING, "Malformed lang file name: " + file.getName() + ". Expected lang_<language code>(_country).yml");
							continue;
						}
						Locale loc;
						if (raw.length == 2)
							loc = Locale.of(raw[1]);
						else
							loc = Locale.of(raw[1], raw[2]);
						boolean result = localeLoader(file, loc);
						if (result)
							log(Level.INFO, "Loaded language: " + loc);
						else
							log(Level.INFO, "Nothing loaded from " + file.getName());
						if (conf.general.defaultLocale.equals(loc) && result) {
							hasDefaultLocale = true;
						}
					}
				}

				finalResult = true;
			}
		}
		catch (IOException | InvalidConfigurationException ex) {
			ex.printStackTrace();
        }
		finally {
			if (!hasDefaultLocale) {
				Level msgLevel = disableOnFail ? Level.SEVERE : Level.WARNING;
				log(msgLevel, "Could not load the default locale file!");
				if (disableOnFail) {
					setEnabled(false);
					getServer().getPluginManager().disablePlugin(this);
				}
				finalResult = false;
			}
		}

		return finalResult;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		perms = rsp.getProvider();
		return true;
	}

	public SunUtil getSunUtil() {
		return rayTraceTaskInstance.getSunUtil();
	}

	@Override
	public void onEnable() {
		// ProtocolLib compat
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
			plc = new ProtocolLibCompat(this);
		else
			log(Level.WARNING, "ProtocolLib plugin not detected. Disabled ProtocolLib compat.");

		isDisguiseEnabled = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");

		if (conf.compatibility.useVampirePermGroup) {
			hasVault = getServer().getPluginManager().getPlugin("Vault") != null;
			if (hasVault) {
				if (setupPermissions()) {
					if (perms.hasGroupSupport()) {
						log(Level.INFO, "Successfully hooked with Vault permission service!");
					}
					else {
						log(Level.WARNING, "Your permission plugin doesn't support groups! Vampire permission group won't be available.");
						perms = null;
					}
				}
				else {
					log(Level.WARNING, "No permission plugin compatible with Vault could be found! Vampire permission group won't be available.");
				}
			}
			else {
				log(Level.WARNING, "You need Vault plugin to enable vampire permission group!");
			}
		}

		loadCompat();
		registerCommands();

		altarDark = new AltarDark(this);
		altarLight = new AltarLight(this);

		// PlaceholderAPI
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			expansionPAPI = new VampireExpansion(this);
			expansionPAPI.register();
		}

		// Listener
		Bukkit.getPluginManager().registerEvents(new ListenerMain(this), this);


		BukkitScheduler scheduler = getServer().getScheduler();

		cleanTask = scheduler.runTaskTimer(this, () -> vPlayerManager.saveAll(SaveOperation.SAVE_ALL_AND_REMOVE_INACTIVE), 0L, 5 * 60 * 20L);
		theTask = scheduler.runTaskTimer(this, new TheTask(this), 0L, (this.conf.general.taskDelayMillis * 20L) / 1000);
		batTask = scheduler.runTaskTimer(this, new BatTask(this), 0L, (this.conf.general.batTaskDelayMillis * 20L) / 1000);
		if (!conf.radiation.useOldRadiationFormula) {
			rayTraceTaskInstance = new RaytracingTask(this);
			rayTraceTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, rayTraceTaskInstance, 50L, 50L);
		}
	}

	private void registerCommands() {
		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			dispatcher = commands.registrar().getDispatcher();
			commands.registrar().register(CmdVampire.VAMPIRE_COMMAND, List.of("vampire", "v"));
		});
	}

	public static YamlConfiguration fileToYamlConfig (File file) throws IOException, InvalidConfigurationException {
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.load(file);

		return yamlConfiguration;
	}

	public boolean localeLoader(File file, Locale locale) throws IOException, InvalidConfigurationException {
		Map<String, String> localeFile = new HashMap<>();
		YamlConfiguration config = fileToYamlConfig(file);
		int maxFeels = 0,
			maxHints = 0;
		boolean hasOneFeel = false,
				hasOneHint = false;

		for (String configKey : config.getKeys(false)) {
			ConfigurationSection inner = config.getConfigurationSection(configKey);
			if (inner == null) {
				continue;
			}

			configKey = configKey.toLowerCase();
			for (String key : inner.getKeys(false)) {
				String value = inner.getString(key);
				key = key.toLowerCase();
				if (value != null && !value.isEmpty()) {
					localeFile.put(configKey + "." + key, value);
					if (configKey.equals("infection")) {
						if (key.startsWith("feeling")) {
							if (key.equals("feeling"))
								hasOneFeel = true;
							maxFeels++;
						}
						else if (key.startsWith("hint")) {
							if (key.equals("hint"))
								hasOneHint = true;
							maxHints++;
						}
					}
					debugLog(Level.INFO, "[" + locale.toString() +
							"] Loaded key " + configKey + "." + key +
							" with value " + value);
				}
			}
		}

		if (localeFile.isEmpty())
			return false;
		locales.put(locale, localeFile);
		if (hasOneFeel && maxFeels > 1) {
			log(Level.WARNING, "Locale [" + locale.toString() + "] has an infection feeling without index alongside indexed feelings. Only that feeling will be used");
			maxFeels = -1;
		}
		if (hasOneHint && maxHints > 1) {
			log(Level.WARNING, "Locale [" + locale.toString() + "] has an infection hint without index alongside indexed hints. Only that hint will be used");
			maxHints = -1;
		}
		maxFeelHint.put(locale, new Tuple<>(maxFeels, maxHints));

		return true;
	}

	public void log(Level level, String rawMessage) {
		rawMessage = "[" + getName() + "] " + rawMessage;
		TextColor color = null;
		if (level == Level.SEVERE)
			color = NamedTextColor.RED;
		else if (level == Level.WARNING)
			color = NamedTextColor.YELLOW;
		else if (level == Level.CONFIG)
			color = NamedTextColor.GREEN;

		Component message = MiniMessage.miniMessage().deserialize(rawMessage);
		if (color != null)
			message = message.colorIfAbsent(color);

		Bukkit.getConsoleSender().sendMessage(message);
	}

	public void debugLog(Level level, String message) {
		if (getVampireConfig().general.debug)
			log(level, message);
	}

	public static VampireRevamp getInstance() {
		return (VampireRevamp) Bukkit.getPluginManager().getPlugin("VampireRevamp");
	}

	public PluginConfig getVampireConfig() {
		return conf;
	}

	public File getPlayerFolder() {
		return new File(this.getDataFolder(), "store");
	}

	@Override
	public void onDisable() {
		if (cleanTask != null)
			cleanTask.cancel();
		if (theTask != null)
			theTask.cancel();
		if (batTask != null)
			batTask.cancel();
		if (rayTraceTaskInstance != null)
			rayTraceTaskInstance.stop();
		if (rayTraceTask != null)
			rayTraceTask.cancel();

		if (vPlayerManager != null) {
			log(Level.INFO, "Saving player data...");
			vPlayerManager.stopRunningTasks();
			vPlayerManager.saveAllSync(SaveOperation.SAVE_ALL);
			log(Level.INFO, "Saved!");
		}
	}

	public void sendMessage(CommandSender recipient, MessageType type, KeyProvider key, @Nullable Integer index, String... replacements) {
		Component message = getMessage(recipient, key, index, replacements);
		sendMessage(recipient, message, type);
	}

	public void sendMessage(CommandSender recipient, MessageType type, KeyProvider key, String... replacements) {
		sendMessage(recipient, type, key, null, replacements);
	}

	@SafeVarargs
    public final void sendMessage(CommandSender recipient, MessageType type, KeyProvider key, @Nullable Integer index, Tuple<String, Component>... replacements) {
		Component message = getMessage(recipient, key, index, replacements);
		sendMessage(recipient, message, type);
	}

	@SafeVarargs
	public final void sendMessage(CommandSender recipient, MessageType type, KeyProvider key, Tuple<String, Component>... replacements) {
		sendMessage(recipient, type, key, null, replacements);
	}

	public void sendMessage(CommandSender recipient, MessageType type, KeyProvider key) {
		sendMessage(recipient, type, key, (Integer) null);
	}

	public void sendMessage(CommandSender recipient, MessageType type, KeyProvider key, @Nullable Integer index) {
		Component message = getMessage(recipient, key, index);
		sendMessage(recipient, message, type);
	}

	private void sendMessage(CommandSender recipient, Component message, MessageType type) {
		NamedTextColor color = null;
		if (type == MessageType.ERROR)
			color = NamedTextColor.RED;
		else if (type == MessageType.SYNTAX)
			color = NamedTextColor.YELLOW;
		else if (type == MessageType.HELP)
			color = NamedTextColor.AQUA;

		if (color != null)
			message = message.colorIfAbsent(color);

		recipient.sendMessage(message);
	}

	@SafeVarargs
	public final List<Component> getMessageList(CommandSender recipient, KeyProvider key, Tuple<String, Component>... replacements) {
		Locale locale = conf.general.defaultLocale;
		if (recipient instanceof Player player)
			locale = player.locale();
		Map<String, String> messages = locales.get(locale);
		if (messages == null)
			messages = locales.get(Locale.of(locale.getCountry()));
		if (messages == null)
			messages = locales.get(conf.general.defaultLocale);
		String rawMessage = messages.get(key.getKey());
		if (rawMessage == null) {
			messages = locales.get(conf.general.defaultLocale);
			rawMessage = messages.get(key.getKey());
		}
		if (rawMessage == null)
			rawMessage = "<red>Key \"" + key.getKey() + "\" undefined for your locale and default locale. Please ask an admin for support.";

		rawMessage = rawMessage.replaceAll("\\r?\\n", "\n");
		String[] rawMessages = rawMessage.split("\\r?\\n");
        return Arrays.stream(rawMessages)
				.map((rm) -> MiniMessage.miniMessage().deserialize(rm))
				.map((comp) -> {
					for (Tuple<String, Component> rep : replacements) {
						comp = comp.replaceText((config) -> config.matchLiteral(rep.x).replacement(rep.y));
					}
					return comp;
				})
				.collect(Collectors.toList());
	}

	/*
	 * Returns < 0 if not indexed, null if not found and positive number if found
	 */
	public Integer getNumValues(CommandSender recipient, KeyProvider key) {
		Locale locale = conf.general.defaultLocale;

		if (key != InfectionMessageKeys.FEELING && key != InfectionMessageKeys.HINT)
			return -1;

		if (recipient instanceof Player player)
			locale = player.locale();
		Tuple<Integer, Integer> maxes = maxFeelHint.get(locale);
		if (maxes == null ||
				(key == InfectionMessageKeys.FEELING && maxes.x == 0) ||
				(key == InfectionMessageKeys.HINT && maxes.y == 0))
			maxes = maxFeelHint.get(Locale.of(locale.getCountry()));
		if (maxes == null ||
				(key == InfectionMessageKeys.FEELING && maxes.x == 0) ||
				(key == InfectionMessageKeys.HINT && maxes.y == 0))
			maxes = maxFeelHint.get(conf.general.defaultLocale);
		if (maxes == null ||
				(key == InfectionMessageKeys.FEELING && maxes.x == 0) ||
				(key == InfectionMessageKeys.HINT && maxes.y == 0))
			return null;
		return key == InfectionMessageKeys.FEELING ? maxes.x : maxes.y;
	}

	public Component getMessage(CommandSender recipient, KeyProvider key, @Nullable Integer index) {
		Locale locale = conf.general.defaultLocale;
		String strKey = key.getKey().toLowerCase();
		if (index != null && index >= 0)
			strKey += index;

		if (recipient instanceof Player player)
			locale = player.locale();
		Map<String, String> messages = locales.get(locale);
		if (messages == null) {
			messages = locales.get(Locale.of(locale.getCountry()));
		}
		if (messages == null) {
			messages = locales.get(conf.general.defaultLocale);
		}

		String rawMessage = messages.get(strKey);
		if (rawMessage == null) {
			messages = locales.get(conf.general.defaultLocale);
			rawMessage = messages.get(strKey);
		}
		if (rawMessage == null)
			rawMessage = "<red>Key \"" + strKey + "\" undefined for your locale and default locale. Please ask an admin for support.";

		rawMessage = rawMessage.replaceAll("\\r?\\n", "\n");
		return MiniMessage.miniMessage().deserialize(rawMessage);
	}

	public Component getMessage(CommandSender recipient, KeyProvider key) {
		return getMessage(recipient, key, (Integer) null);
	}

	public Component getMessage(CommandSender recipient, KeyProvider key, @Nullable Integer index, String... replacements) {
		if (replacements.length % 2 == 1)
			throw new IllegalArgumentException("Odd number of replacements. Please contact the dev and send them the whole error");
		Tuple<String, Component>[] tuples = new Tuple[replacements.length / 2];
		for (int i = 0; i < tuples.length; i++) {
			tuples[i] = new Tuple<>(replacements[2 * i], Component.text(replacements[2 * i + 1]));
		}
		return getMessage(recipient, key, tuples);
	}

	public Component getMessage(CommandSender recipient, KeyProvider key, String... replacements) {
		return getMessage(recipient, key, null, replacements);
	}

	@SafeVarargs
	public final Component getMessage(CommandSender recipient, KeyProvider key, @Nullable Integer index, Tuple<String, Component>... replacements) {
		Component message = getMessage(recipient, key, index);
		for (Tuple<String, Component> replacement : replacements) {
			message = message.replaceText(
					(config) -> config.matchLiteral(replacement.x).replacement(replacement.y)
			);
		}
		return message;
	}

	@SafeVarargs
	public final Component getMessage(CommandSender recipient, KeyProvider key, Tuple<String, Component>... replacements) {
		return getMessage(recipient, key, null, replacements);
	}

	@NotNull
	public Component[] getYouAreWere(@NotNull CommandSender sender, @NotNull OfflinePlayer target, boolean self) {
		Component you;
		KeyProvider areKey;
		KeyProvider wereKey;
		if (self) {
			you = TextUtil.capitalizeFirst(getMessage(sender, GrammarMessageKeys.YOU));
			areKey = GrammarMessageKeys.TO_BE_2ND;
			wereKey = GrammarMessageKeys.TO_BE_2ND_PAST;
		}
		else {
			String name = target.getName();
			you = Component.text(name != null ? name : "UNKNOWN");
			areKey = GrammarMessageKeys.TO_BE_3RD;
			wereKey = GrammarMessageKeys.TO_BE_3RD_PAST;
		}
		Component are = getMessage(sender, areKey);
		Component were = getMessage(sender, wereKey);

		return new Component[] {you, are, were};
	}

	public void saveVPlayer(@Nullable UUID uuid) {
		if (uuid == null)
			return;
		vPlayerManager.save(uuid);
	}

	public void saveVPlayer(@Nullable Player player) {
		if (player == null)
			return;
		saveVPlayer(player.getUniqueId());
	}

	public void saveVPlayer(@Nullable OfflinePlayer offlinePlayer) {
		if (offlinePlayer == null)
			return;
		saveVPlayer(offlinePlayer.getUniqueId());
	}

	@Nullable
	@Contract(value = "null -> null")
	public VPlayer getVPlayer(@Nullable UUID uuid) {
		if (uuid == null)
			return null;
		return playerCollection.computeIfAbsent(uuid, (k) -> vPlayerManager.tryGetDataNow(k));
	}

	@Nullable
	@Contract(value = "null -> null")
	public VPlayer getVPlayer(@Nullable Player player) {
		if (player == null)
			return null;
		return getVPlayer(player.getUniqueId());
	}

	@Nullable
	@Contract(value = "null -> null")
	public VPlayer getVPlayer(@Nullable OfflinePlayer offlinePlayer) {
		if (offlinePlayer == null)
			return null;
		return getVPlayer(offlinePlayer.getUniqueId());
	}

	/**
	 * This method is for VampireRevamp internal use. If you break something, don't tell me I didn't warn you
	 * @param vPlayer the data to store
	 * @return the previous stored value
	 */
	@Nullable
	public VPlayer storeVPlayer(@NotNull VPlayer vPlayer) {
		return playerCollection.put(vPlayer.getUuid(), vPlayer);
	}

	/**
	 * Use this method at your own peril
	 * @param uuid the UUID of the player to load
	 */
	public void loadVPlayerFromDB(@NotNull UUID uuid) {
		vPlayerManager.loadData(uuid);
	}
}