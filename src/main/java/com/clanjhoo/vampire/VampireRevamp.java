package com.clanjhoo.vampire;

import co.aikar.commands.*;
import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import com.clanjhoo.dbhandler.data.DBObjectManager;
import com.clanjhoo.dbhandler.data.SaveOperation;
import com.clanjhoo.dbhandler.data.StorageType;
import com.clanjhoo.vampire.compat.*;
import com.clanjhoo.vampire.config.StorageConfig;
import com.clanjhoo.vampire.event.VampireLoadedEvent;
import com.clanjhoo.vampire.listeners.*;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import com.clanjhoo.vampire.altar.AltarDark;
import com.clanjhoo.vampire.altar.AltarLight;
import com.clanjhoo.vampire.cmd.CmdVampire;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;

import com.clanjhoo.vampire.tasks.BatTask;
import com.clanjhoo.vampire.tasks.TheTask;
import com.clanjhoo.vampire.util.*;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

	private AudienceProvider adventure;
	private int cleanTaskId = -1;
	private int theTaskId = -1;
	private int batTaskId = -1;
	private PaperCommandManager manager;
	public final Map<UUID, Boolean> batEnabled = new ConcurrentHashMap<>();
	public final Set<LivingEntity> bats = new HashSet<>();
	public final Map<UUID, List<LivingEntity>> batmap = new ConcurrentHashMap<>();
	private boolean isDisguiseEnabled = false;
	private boolean hasVault = false;
	private PluginConfig conf = null;
	private AltarDark altarDark;
	private AltarLight altarLight;
	private SemVer serverVersion;
	private boolean disabled = false;
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


	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //


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
			disabled = true;
			return;
		}

		try {
			vc = new VersionCompat(this, serverVersion);
			// Test versionCompat is working
			vc.test();
		}
		catch (Exception ex) {
			log(Level.SEVERE, "Error found while loading methods for minecraft version: " + versionString);
			ex.printStackTrace();
			setEnabled(false);
			disabled = true;
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

		// WorldGuard compat
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)
			wg = new WorldGuardCompat(this);
		else
			log(Level.WARNING, "WorldGuard plugin not detected. Disabled WorldGuard compat.");

		// ProtocolLib compat
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null)
			plc = new ProtocolLibCompat(this);
		else
			log(Level.WARNING, "ProtocolLib plugin not detected. Disabled ProtocolLib compat.");

		try {
			File localesFolder = new File(this.getDataFolder() + "/locales");
			if (!localesFolder.exists())
				localesFolder.mkdir();
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
	}

	@Override
	public void saveDefaultConfig() {
		File dataFolder = this.getDataFolder();
		File configFile = new File(dataFolder, "config.yml");
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		if (!configFile.exists()) {
			PluginConfig conf = new PluginConfig(this);
			conf.saveConfigToFile(configFile);
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
			} catch (IOException ex) {
				log(Level.SEVERE, "Couldn't create storage! Disabling plugin!");
				ex.printStackTrace();
				getServer().getPluginManager().disablePlugin(this);
				disabled = true;
				return false;
			}
		}
		if (reloadPlayers) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				loadVPlayerFromDB(p.getUniqueId());
			}
		}

		return result;
	}

	private boolean loadLocales(boolean disableOnFail) {
		boolean hasDefaultLocale = false;
		boolean finalResult = false;
		try {
			File folder = new File(this.getDataFolder() + "/locales");
			File[] files = folder.listFiles();
			if (files != null) {
				for (final File file : files) {
					if (file.isFile()) {
						String[] raw = file.getName().split("\\.");
						if (raw.length != 2 || (!raw[1].equalsIgnoreCase("yml") && !raw[1].equalsIgnoreCase("yaml")))
							continue;
						raw = raw[0].split("_");
						if (raw.length != 2 || !raw[0].equalsIgnoreCase("lang"))
							continue;
						Locale loc = new Locale(raw[1]);
						manager.addSupportedLanguage(loc);
						if (conf.general.debug)
							localeLoader(file, loc);
						boolean result = manager.getLocales().loadYamlLanguageFile(file, loc);
						if (result) {
							log(Level.INFO, "Loaded language: " + loc);
						}
						else {
							log(Level.INFO, "Nothing loaded from " + file.getName());
						}
						if (conf.general.defaultLocale.equals(loc) && result) {
							hasDefaultLocale = true;
							manager.getLocales().setDefaultLocale(conf.general.defaultLocale);
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
				log(msgLevel, "Couldn't load the default locale file!");
				if (disableOnFail) {
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

	@Override
	public void onEnable()
	{
		if (disabled) {
			return;
		}

		// Initialize an audiences instance for the plugin
		this.adventure = BukkitAudiences.create(this);

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

		manager = new PaperCommandManager(this);

		manager.enableUnstableAPI("help");
		setFormatting(manager);
		manager.addSupportedLanguage(conf.general.defaultLocale);
		manager.usePerIssuerLocale(true);
		if (!loadLocales(true))
			return;

		CmdVampire baseCommand = new CmdVampire(this);
		manager.registerCommand(baseCommand);
		baseCommand.initialize();
		manager.getCommandCompletions().registerAsyncCompletion("yesno", c -> List.of("yes", "no"));
		manager.getCommandCompletions().registerAsyncCompletion("reloads", c -> List.of("locales", "config", "all"));

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

		cleanTaskId = scheduler.scheduleSyncRepeatingTask(this, () -> vPlayerManager.saveAll(SaveOperation.SAVE_ALL_AND_REMOVE_INACTIVE), 0L, 5 * 60 * 20L);
		theTaskId = scheduler.scheduleSyncRepeatingTask(this, new TheTask(this), 0L, (this.conf.general.taskDelayMillis * 20L) / 1000);
		batTaskId = scheduler.scheduleSyncRepeatingTask(this, new BatTask(this), 0L, (this.conf.general.batTaskDelayMillis * 20L) / 1000);
	}

	public static YamlConfiguration fileToYamlConfig (File file) throws IOException, InvalidConfigurationException {
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		yamlConfiguration.load(file);

		return yamlConfiguration;
	}

	public boolean localeLoader(File file, Locale locale) {
		boolean loaded = false;
		try {
			YamlConfiguration config = fileToYamlConfig(file);

			for (String configKey : config.getKeys(false)) {
				ConfigurationSection inner = config.getConfigurationSection(configKey);
				if (inner == null) {
					continue;
				}

				for (String key : inner.getKeys(false)) {
					String value = inner.getString(key);
					if (value != null && !value.isEmpty()) {
						MessageKey loadedKey = MessageKey.of(configKey + "." + key);
						debugLog(Level.INFO, "[" + locale.toString() +
								"] Loaded key " + loadedKey.getKey() +
								" with value " + value);
						loaded = true;
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return loaded;
	}

	public void log(Level level, String rawMessage) {
		if (adventure == null) {
			getLogger().log(level, rawMessage);
			return;
		}
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

		adventure.console().sendMessage(message);
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

	public PaperCommandManager getCommandManager() {
		return manager;
	}

	public File getPlayerFolder() {
		return new File(this.getDataFolder(), "store");
	}

	@Override
	public void onDisable() {
		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
		if (disabled) {
			return;
		}
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.cancelTask(cleanTaskId);
		scheduler.cancelTask(theTaskId);
		scheduler.cancelTask(batTaskId);

		if (vPlayerManager != null) {
			log(Level.INFO, "Saving player data...");
			vPlayerManager.stopRunningTasks();
			vPlayerManager.saveAllSync(SaveOperation.SAVE_ALL);
			log(Level.INFO, "Saved!");
		}
	}

	@SuppressWarnings("deprecation")
	public void setFormatting(CommandManager<?, ?, ChatColor, ?, ?, ?> manager) {
		Set<MessageType> messageTypes = getMessageTypes();

		for (MessageType mtype : messageTypes) {
			manager.setFormat(mtype, 1, ChatColor.RESET);
			int ordis = ChatColor.RESET.ordinal();
			for (ChatColor cc : ChatColor.values()) {
				int thisIndex = 1 + cc.ordinal();
				if (cc.ordinal() < ordis)
					thisIndex += 1;
				if (cc.ordinal() != ordis) {
					manager.setFormat(mtype, thisIndex, cc);
				}
			}
		}
	}

	public Set<MessageType> getMessageTypes() {
		return CollectionUtil.set(
				MessageType.ERROR,
				MessageType.HELP,
				MessageType.INFO,
				MessageType.SYNTAX
		);
	}

	private CommandIssuer getCommandIssuer(CommandSender sender) {
		return manager.getCommandIssuer(sender);
	}

	public void sendMessage(CommandSender recipient, Component message) {
		if (recipient instanceof Player) {
			adventure.player(((Player) recipient).getUniqueId()).sendMessage(message);
		} else if (recipient instanceof ConsoleCommandSender) {
			adventure.console().sendMessage(message);
		} else {
			recipient.spigot().sendMessage(BungeeComponentSerializer.get().serialize(message));
		}
	}

	public void sendMessage(CommandSender recipient, MessageType type, MessageKeyProvider keyProvider, String... replacements) {
		Component message = getMessage(recipient, keyProvider, replacements);
		sendMessage(recipient, message, type);
	}

	@SafeVarargs
    public final void sendMessage(CommandSender recipient, MessageType type, MessageKeyProvider keyProvider, Tuple<String, Component>... replacements) {
		Component message = getMessage(recipient, keyProvider, replacements);
		sendMessage(recipient, message, type);
	}

	public void sendMessage(CommandSender recipient, MessageType type, MessageKeyProvider keyProvider) {
		Component message = getMessage(recipient, keyProvider);
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

		sendMessage(recipient, message);
	}

	@SafeVarargs
	public final List<Component> getMessageList(CommandSender recipient, MessageKeyProvider keyProvider, Tuple<String, Component>... replacements) {
		String[] rawMessages = getCommandManager()
				.getLocales()
				.getMessage(getCommandIssuer(recipient), keyProvider)
				.split("\\r?\\n");
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

	public Component getMessage(CommandSender recipient, MessageKeyProvider keyProvider) {
		String rawMessage =  getCommandManager()
				.getLocales()
				.getMessage(getCommandIssuer(recipient), keyProvider)
				.replaceAll("\\r?\\n", "\n");
		return MiniMessage.miniMessage().deserialize(rawMessage);
	}

	public Component getMessage(CommandSender recipient, MessageKeyProvider keyProvider, String... replacements) {
		if (replacements.length % 2 == 1)
			throw new IllegalArgumentException("Odd number of replacements. Please contact the dev and send them the whole error");
		Tuple<String, Component>[] tuples = new Tuple[replacements.length / 2];
		for (int i = 0; i < tuples.length; i++) {
			tuples[i] = new Tuple<>(replacements[2 * i], Component.text(replacements[2 * i + 1]));
		}
		return getMessage(recipient, keyProvider, tuples);
	}

	@SafeVarargs
    public final Component getMessage(CommandSender recipient, MessageKeyProvider keyProvider, Tuple<String, Component>... replacements) {
		Component message = getMessage(recipient, keyProvider);
        for (Tuple<String, Component> replacement : replacements) {
            message = message.replaceText(
					(config) -> config.matchLiteral(replacement.x).replacement(replacement.y)
			);
        }
		return message;
	}

	@NotNull
	public Component[] getYouAreWere(@NotNull CommandSender sender, @NotNull OfflinePlayer target, boolean self) {
		Component you;
		MessageKeyProvider areKey;
		MessageKeyProvider wereKey;
		if (self) {
			you = TextUtil.capitalizeFirst(getMessage(sender, GrammarMessageKeys.YOU));
			areKey = GrammarMessageKeys.TO_BE_2ND;
			wereKey = GrammarMessageKeys.TO_BE_2ND_PAST;
		}
		else {
			you = Component.text(target.getName());
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