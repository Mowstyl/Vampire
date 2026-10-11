package com.clanjhoo.vampire.cmd;

import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.cmd.arguments.ReloadArgumentType;
import com.clanjhoo.vampire.cmd.arguments.YesNoArgumentType;
import com.clanjhoo.vampire.keyproviders.*;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

import static com.clanjhoo.vampire.util.CommandUtils.*;


public class CmdVampire {
	private static VampireRevamp plugin = null;
	private static VampireRevamp getPlugin() {
		if (plugin == null)
			plugin = VampireRevamp.getInstance();
		return plugin;
	}

	private static TextUtil textUtil = null;
	private static TextUtil getTextUtil() {
		if (textUtil == null)
			textUtil = TextUtil.get(getPlugin());
		return textUtil;
	}

	private static ResourceUtil resUtil = null;
	private static ResourceUtil getResourceUtil() {
		if (resUtil == null)
			resUtil = ResourceUtil.get(getPlugin());
		return resUtil;
	}

	private static BloodFlaskUtil bloodUtil = null;
	private static BloodFlaskUtil getBloodFlaskUtil() {
		if (bloodUtil == null)
			bloodUtil = BloodFlaskUtil.get(getPlugin());
		return bloodUtil;
	}

	private static HolyWaterUtil holyUtil = null;
	private static HolyWaterUtil getHolyWaterUtil() {
		if (holyUtil == null)
			holyUtil = HolyWaterUtil.get(getPlugin());
		return holyUtil;
	}

	private static SunUtil sunUtil = null;
	private static SunUtil getSunUtil() {
		if (sunUtil == null)
			sunUtil = SunUtil.get(getPlugin());
		return sunUtil;
	}

	private static RingUtil ringUtil = null;
	private static RingUtil getRingUtil() {
		if (ringUtil == null)
			ringUtil = RingUtil.get(getPlugin());
		return ringUtil;
	}

	public static final LiteralCommandNode<CommandSourceStack> reload = Commands
			.literal("reload")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_RELOAD.getPermissionNode()))
			.executes(CmdVampire::reloadCommand)
			.then(Commands.argument("what", ReloadArgumentType.reloadClass())
					.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_RELOAD.getPermissionNode()))
					.executes(CmdVampire::reloadCommand))
			.build();
	public static final CommandMessageKeys reloadDesc = CommandMessageKeys.RELOAD_DESCRIPTION;
	public static int reloadCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		int result = 0;
		CommandSender sender = ctx.getSource().getSender();

		ReloadArgumentType.ReloadType reloadType;
		try {
			reloadType = ctx.getArgument("what", ReloadArgumentType.ReloadType.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
			reloadType = ReloadArgumentType.ReloadType.ALL;
		}

		boolean success = switch (reloadType) {
            case CONFIG -> getPlugin().reloadVampireConfig();
			case LOCALES -> getPlugin().reloadLocales();
            case ALL -> getPlugin().reloadAll();
		};

		if (success) {
			result = Command.SINGLE_SUCCESS;
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.RELOAD_SUCCESS);
		}
		else {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.RELOAD_FAIL);
		}

		return result;
	}


	private static int showVampireData(CommandSender sender, OfflinePlayer offlinePlayer, VPlayer vPlayer, boolean self) {
		PluginConfig conf = getPlugin().getVampireConfig();
		Component[] youAreWere = getPlugin().getYouAreWere(sender, offlinePlayer, self);
		Component you = youAreWere[0];
		Component are = youAreWere[1];
		Component were = youAreWere[2];
		Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
		Component on = getPlugin().getMessage(sender, GrammarMessageKeys.ON);
		Component off = getPlugin().getMessage(sender, GrammarMessageKeys.OFF);

		String name = offlinePlayer.getName();
		Component playerName = Component.text(name != null ? name : "UNKNOWN");

		sender.sendMessage(getTextUtil().getPlayerInfoHeader(vPlayer.isVampire(),
				vPlayer.isNosferatu(),
				playerName,
				sender));
		if (vPlayer.isVampire()) {
			if (vPlayer.isNosferatu())
				vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_TYPE,
					new Tuple<>("{player}", you),
					new Tuple<>("{to_be}", are),
					new Tuple<>("{vampire_type}", vampireType));

			InfectionReason reason = vPlayer.getReason();
			String parent = null;
			if (reason.isMaker()) {
				parent = vPlayer.getMakerName();
			}
			if (parent == null || parent.isEmpty()) {
				parent = "someone";
			}
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					reason.getDescKey(),
					new Tuple<>("{player}", you),
					new Tuple<>("{to_be_past}", were),
					new Tuple<>("{parent}", Component.text(parent)));

			Component bloodlustName = getPlugin().getMessage(sender, GrammarMessageKeys.BLOODLUST);
			bloodlustName = TextUtil.capitalizeFirst(bloodlustName);
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_BLOODLUST,
					new Tuple<>("{bloodlust}", bloodlustName),
					new Tuple<>("{enabled}", vPlayer.isBloodlusting() ? on : off),
					new Tuple<>("{percent}", Component.text(String.format("%.1f%%", vPlayer.combatDamageFactor() * 100))));

			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_INTENT,
					new Tuple<>("{enabled}", vPlayer.isIntending() ? on : off),
					new Tuple<>("{percent}", Component.text(String.format("%.1f%%", vPlayer.combatInfectRisk() * 100))));

			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_NIGHTVISION,
					new Tuple<>("{enabled}", vPlayer.isUsingNightVision() ? on : off));

			if (offlinePlayer instanceof Player player) {
                getPlugin().sendMessage(sender,
						MessageType.INFO,
						CommandMessageKeys.SHOW_TEMPERATURE,
						"{percent}", String.format("%d%%", (int) Math.round(vPlayer.getTemp() * 100)));

				int rad = (int) Math.round(100 * vPlayer.getRad());
				int sun = (int) Math.round(100 * getSunUtil().calcSolarRad(player.getWorld(), player));
				double terrain = 1d - getSunUtil().calcTerrainOpacity(player.getLocation().getBlock(), vPlayer.getLastRayTrace());
				double armor = 1d - getSunUtil().calcArmorOpacity(player);
				int base = (int) Math.round(100 * conf.radiation.baseRadiation);

				getPlugin().sendMessage(sender,
						MessageType.INFO,
						CommandMessageKeys.SHOW_RADIATION_KEYS);

				getPlugin().sendMessage(sender,
						MessageType.INFO,
						CommandMessageKeys.SHOW_RADIATION_VALUES,
						"{rads}", String.format("%+d%%", rad),
						"{sun}", String.format("%d", sun),
						"{terrain}", String.format("%.2f", terrain),
						"{armor}", String.format("%.2f", armor),
						"{base}", String.format("%+d", base));
			}
		} else if (vPlayer.isInfected()) {
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_INFECTED,
					new Tuple<>("{player}", you),
					new Tuple<>("{to_be}", are),
					new Tuple<>("{percent}", Component.text(String.format("%d%%", Math.round(vPlayer.getInfection() * 100)))));

			InfectionReason reason = vPlayer.getReason();
			String parent = null;
			if (reason.isMaker()) {
				parent = vPlayer.getMakerName();
			}
			if (parent == null || parent.isEmpty()) {
				parent = "someone";
			}
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					reason.getDescKey(),
					new Tuple<>("{player}", you),
					new Tuple<>("{to_be_past}", were),
					new Tuple<>("{parent}", Component.text(parent)));
		} else {
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SHOW_CURED,
					new Tuple<>("{player}", you),
					new Tuple<>("{to_be}", are),
					new Tuple<>("{vampire_type}", vampireType));
		}
		return Command.SINGLE_SUCCESS;
	}

	private static int showCommandLambda(CommandSender sender, OfflinePlayer player, boolean self, int attempt) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			VPlayer data = getPlugin().getVPlayer(player);
			if (data != null) {
				showVampireData(sender, player, data, self);
			}
			else {
				if (attempt < 50)
					showCommandLambda(sender, player, self, attempt + 1);
				else
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND);
			}
		}, 2);
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> show = Commands
			.literal("show")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SHOW.getPermissionNode()))
			.executes(CmdVampire::showCommand)
			.then(Commands.argument("targets", ArgumentTypes.playerProfiles())
					.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SHOW_OTHERS.getPermissionNode()))
					.executes(CmdVampire::showCommand))
			.build();
	public static final CommandMessageKeys showDesc = CommandMessageKeys.SHOW_DESCRIPTION;
	public static int showCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		Collection<PlayerProfile> profiles = null;
		try {
			profiles = ctx.getArgument("targets", PlayerProfileListResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (profiles == null) {
			if (executor instanceof Player p)
				profiles = List.of(p.getPlayerProfile());
			else
				throw EXECUTOR_NOT_PLAYER_OPTIONAL.create();
		}
		if (profiles.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (PlayerProfile profile : profiles) {
			if (profile.getId() == null)
				continue;
			OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getId());
			boolean self = sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId());

			VPlayer vPlayer = getPlugin().getVPlayer(target);
			if (vPlayer == null)
				result += showCommandLambda(sender, target, self, 0);
			else
				result += showVampireData(sender, target, vPlayer, self);
		}

		if (result != 1)
			sendMessage(sender, executor, "Info printed for " + result + " players.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> bloodlust = Commands
			.literal("bloodlust")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_BLOODLUST.getPermissionNode()))
			.executes(CmdVampire::bloodlustCommand)
			.then(Commands.argument("enable", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::bloodlustCommand))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> modebloodlust = Commands
			.literal("modebloodlust")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_BLOODLUST.getPermissionNode()))
			.executes(CmdVampire::bloodlustCommand)
			.redirect(bloodlust)
			.build();
	public static final LiteralCommandNode<CommandSourceStack> b = Commands
			.literal("b")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_BLOODLUST.getPermissionNode()))
			.executes(CmdVampire::bloodlustCommand)
			.redirect(bloodlust)
			.build();
	public static final CommandMessageKeys bloodlustDesc = CommandMessageKeys.BLOODLUST_DESCRIPTION;
	public static int bloodlustCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		Boolean bloodlustEnabled = null;
		try {
			bloodlustEnabled = ctx.getArgument("enable", Boolean.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		getPlugin().debugLog(Level.INFO, "Executed command!");
		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().debugLog(Level.INFO, "Blacklisted world!");
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}

		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		getPlugin().debugLog(Level.INFO, "Acceptance");
		if (vPlayer.isVampire()) {
			getPlugin().debugLog(Level.INFO, "Le vampire");
			boolean isActive = bloodlustEnabled == null ? vPlayer.isBloodlusting() : !bloodlustEnabled;
			vPlayer.setBloodlusting(!isActive);
		} else {
			getPlugin().debugLog(Level.INFO, "Non non non!");
			Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
			Component bloodlustAction = getPlugin().getMessage(sender, GrammarMessageKeys.BLOODLUST);
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
					new Tuple<>("{vampire_type}", vampireType),
					new Tuple<>("{action}", bloodlustAction));
			return 0;
		}

		getPlugin().debugLog(Level.INFO, "End of ZA WARUDO");
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> intend = Commands
			.literal("intend")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_INTENT.getPermissionNode()))
			.executes(CmdVampire::intendCommand)
			.then(Commands.argument("enable", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::intendCommand))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> modeintend = Commands
			.literal("modeintend")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_INTENT.getPermissionNode()))
			.executes(CmdVampire::intendCommand)
			.redirect(intend)
			.build();
	public static final LiteralCommandNode<CommandSourceStack> i = Commands
			.literal("i")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_INTENT.getPermissionNode()))
			.executes(CmdVampire::intendCommand)
			.redirect(intend)
			.build();
	public static final CommandMessageKeys intendDesc = CommandMessageKeys.INTENT_DESCRIPTION;
	public static int intendCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		Boolean intendEnabled = null;
		try {
			intendEnabled = ctx.getArgument("enable", Boolean.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}

		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		if (vPlayer.isVampire()) {
			boolean isActive = intendEnabled == null ? vPlayer.isIntending() : !intendEnabled;
			vPlayer.setIntending(!isActive);
		} else {
			Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
			Component intentAction = getPlugin().getMessage(sender, GrammarMessageKeys.INTEND);
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
					new Tuple<>("{vampire_type}", vampireType),
					new Tuple<>("{action}", intentAction));
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> nightvision = Commands
			.literal("nightvision")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_NIGHTVISION.getPermissionNode()))
			.executes(CmdVampire::nightvisionCommand)
			.then(Commands.argument("enable", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::nightvisionCommand))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> modenightvision = Commands
			.literal("modenightvision")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_NIGHTVISION.getPermissionNode()))
			.executes(CmdVampire::nightvisionCommand)
			.redirect(nightvision)
			.build();
	public static final LiteralCommandNode<CommandSourceStack> nv = Commands
			.literal("nv")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_NIGHTVISION.getPermissionNode()))
			.executes(CmdVampire::nightvisionCommand)
			.redirect(nightvision)
			.build();
	public static final CommandMessageKeys nightvisionDesc = CommandMessageKeys.NIGHTVISION_DESCRIPTION;
	public static int nightvisionCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		Boolean nvEnabled = null;
		try {
			nvEnabled = ctx.getArgument("enable", Boolean.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}
		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}

		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		if (vPlayer.isVampire()) {
			boolean isActive = nvEnabled == null ? vPlayer.isUsingNightVision() : !nvEnabled;
			vPlayer.setUsingNightVision(!isActive);
		} else {
			Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
			Component nvAction = getPlugin().getMessage(sender, GrammarMessageKeys.NIGHTVISION);
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
					new Tuple<>("{vampire_type}", vampireType),
					new Tuple<>("{action}", nvAction));
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> offer = Commands
			.literal("offer")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_TRADE_OFFER.getPermissionNode()))
			.then(Commands.argument("target", ArgumentTypes.player())
					.executes(CmdVampire::offerCommand)
					.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 20))
							.executes(CmdVampire::offerCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> o = Commands
			.literal("o")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_TRADE_OFFER.getPermissionNode()))
			.redirect(offer)
			.build();
	public static final CommandMessageKeys offerDesc = CommandMessageKeys.OFFER_DESCRIPTION;
	public static int offerCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		Player pYou = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
				.resolve(ctx.getSource()).getFirst();

		double amount = 4;
		try {
			amount = ctx.getArgument("amount", double.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}
		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}

		VPlayer vMe = getPlugin().getVPlayer(player);
		if (vMe == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		VPlayer vYou = getPlugin().getVPlayer(pYou);
		if (vYou == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		vMe.tradeOffer(player, vYou, amount);
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> accept = Commands
			.literal("accept")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_TRADE_ACCEPT.getPermissionNode()))
			.executes(CmdVampire::acceptCommand)
			.build();
	public static final LiteralCommandNode<CommandSourceStack> a = Commands
			.literal("a")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_TRADE_ACCEPT.getPermissionNode()))
			.executes(CmdVampire::acceptCommand)
			.redirect(accept)
			.build();
	public static final CommandMessageKeys acceptDesc = CommandMessageKeys.ACCEPT_DESCRIPTION;
	public static int acceptCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}
		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		vPlayer.tradeAccept();
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> flask = Commands
			.literal("flask")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_FLASK.getPermissionNode()))
			.executes(CmdVampire::flaskCommand)
			.then(Commands.argument("amount", DoubleArgumentType.doubleArg(0, 20))
					.executes(CmdVampire::flaskCommand))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> f = Commands
			.literal("f")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_FLASK.getPermissionNode()))
			.executes(CmdVampire::flaskCommand)
			.redirect(flask)
			.build();
	public static final CommandMessageKeys flaskDesc = CommandMessageKeys.FLASK_DESCRIPTION;
	public static int flaskCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		double amount = 4;
		try {
			amount = ctx.getArgument("amount", double.class);
			if (amount == 0) {
				sender.sendMessage("¬¬");
				return Command.SINGLE_SUCCESS;
			}
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}
		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}
		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		// Does the player have the required amount?
		boolean consumeFood = getPlugin().getVampireConfig().general.vampiresUseFoodAsBlood && vPlayer.isVampire();
		if ((consumeFood && amount > vPlayer.getFood()) || (!consumeFood && amount > player.getHealth())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					SkillMessageKeys.FLASK_INSUFFICIENT);
			return 0;
		}
		// ... create a blood flask!
		if (getBloodFlaskUtil().fillBottle(vPlayer, amount)) {
			if (consumeFood) {
				vPlayer.addFood(-amount);
			} else {
				player.setHealth(player.getHealth() - amount);
			}
			// Inform
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					SkillMessageKeys.FLASK_SUCCESS);
		}
		else {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					SkillMessageKeys.FLASK_NO_BOTTLE);
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> shriek = Commands
			.literal("shriek")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SHRIEK.getPermissionNode()))
			.executes(CmdVampire::shriekCommand)
			.build();
	public static final CommandMessageKeys shriekDesc = CommandMessageKeys.SHRIEK_DESCRIPTION;
	public static int shriekCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}
		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		if (vPlayer.isVampire()) {
			vPlayer.shriek();
		} else {
			Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
			Component shriekAction = getPlugin().getMessage(sender, GrammarMessageKeys.SHRIEK);
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
					new Tuple<>("{vampire_type}", vampireType),
					new Tuple<>("{action}", shriekAction));
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> list = Commands
			.literal("list")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_LIST.getPermissionNode()))
			.executes(CmdVampire::listCommand)
			.then(Commands.argument("page", IntegerArgumentType.integer(1))
					.executes(CmdVampire::listCommand))
			.build();
	public static final CommandMessageKeys listDesc = CommandMessageKeys.LIST_DESCRIPTION;
	public static int listCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();
		if (executor != null)
			sender = executor;

		int page = 1;
		try {
			page = ctx.getArgument("page", int.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}
		List<Component> vampiresOnline = new ArrayList<>();
		List<Component> infectedOnline = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			VPlayer vPlayer = getPlugin().getVPlayer(player);
			if (vPlayer == null)
				continue;
			Player auxPlayer = vPlayer.getPlayer();
			if (auxPlayer == null)
				continue;

			Component compName = auxPlayer.displayName();
			if (vPlayer.isVampire())
				vampiresOnline.add(compName);
			else if (vPlayer.isInfected())
				infectedOnline.add(compName);
		}

		// Create Messages
		List<Component> lines = new ArrayList<>();

		Component vampireType = getPlugin().getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
		vampireType = TextUtil.capitalizeFirst(vampireType);
		Component infectedType = getPlugin().getMessage(sender, GrammarMessageKeys.INFECTED_TYPE);
		infectedType = TextUtil.capitalizeFirst(infectedType);
		Component onlineStr = getPlugin().getMessage(sender, GrammarMessageKeys.ONLINE);
		//String offlineStr = getPlugin().getMessage(sender, GrammarMessageKeys.OFFLINE);

		if (!vampiresOnline.isEmpty())
		{
			lines.add(Component.text("=== ")
					.append(vampireType)
					.append(Component.text("s ", vampireType.color()))
					.append(onlineStr)
					.append(Component.text(" ==="))
					.color(NamedTextColor.LIGHT_PURPLE));
			lines.add(Component.join(
					JoinConfiguration.builder().separator(
							Component.text(", ")),
							vampiresOnline)
					.color(NamedTextColor.YELLOW));
		}

		if (!infectedOnline.isEmpty())
		{
			lines.add(Component.text("=== ")
					.append(infectedType)
					.append(Component.text("s ", infectedType.color()))
					.append(onlineStr)
					.append(Component.text(" ==="))
					.color(NamedTextColor.LIGHT_PURPLE));
			lines.add(Component.join(
							JoinConfiguration.builder().separator(
									Component.text(", ")),
							infectedOnline)
					.color(NamedTextColor.YELLOW));
		}

		// Send them
		// lines = getTextUtil().wrap(lines);
		for (Component line : lines)
			sender.sendMessage(line);
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> version = Commands
			.literal("version")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_VERSION.getPermissionNode()))
			.executes(CmdVampire::versionCommand)
			.build();
	public static final LiteralCommandNode<CommandSourceStack> v = Commands
			.literal("v")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_VERSION.getPermissionNode()))
			.executes(CmdVampire::versionCommand)
			.redirect(version)
			.build();
	public static final CommandMessageKeys versionDesc = CommandMessageKeys.VERSION_DESCRIPTION;
	public static int versionCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();
		if (executor != null)
			sender = executor;

		List<Component> pd = TextUtil.getPluginDescription(plugin);
		for (Component mess : pd)
			sender.sendMessage(mess);

		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> batusi = Commands
			.literal("batusi")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_BATUSI.getPermissionNode()))
			.executes(CmdVampire::batusiCommand)
			.then(Commands.argument("numberOfBats", IntegerArgumentType.integer(0, getPlugin().getVampireConfig().vampire.batusi.maxBats))
					.executes(CmdVampire::batusiCommand)
					.then(Commands.argument("enableBatusi", YesNoArgumentType.yesNoClass())
							.executes(CmdVampire::batusiCommand)))
			.then(Commands.argument("enableBatusi", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::batusiCommand)
					.then(Commands.argument("numberOfBats", IntegerArgumentType.integer(0, getPlugin().getVampireConfig().vampire.batusi.maxBats))
							.executes(CmdVampire::batusiCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> bat = Commands
			.literal("bat")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_BATUSI.getPermissionNode()))
			.executes(CmdVampire::batusiCommand)
			.redirect(batusi)
			.build();
	public static final CommandMessageKeys batusiDesc = CommandMessageKeys.BATUSI_DESCRIPTION;
	public static int batusiCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		Boolean batusiEnabled = null;
		try {
			batusiEnabled = ctx.getArgument("enableBatusi", Boolean.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		int numberOfBats = getPlugin().getVampireConfig().vampire.batusi.numberOfBats;
		boolean specifiedNum = false;
		try {
			numberOfBats = ctx.getArgument("numberOfBats", int.class);
			specifiedNum = true;
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (getPlugin().getVampireConfig().general.isBlacklisted(player.getWorld())) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return 0;
		}

		VPlayer vPlayer = getPlugin().getVPlayer(player);
		if (vPlayer == null) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
			return 0;
		}
		if (!getPlugin().getVampireConfig().vampire.batusi.nosferatuOnly || vPlayer.isNosferatu()) {
			boolean activate = !getPlugin().batEnabled.getOrDefault(player.getUniqueId(), false);
			int numBats = 0;

			if (batusiEnabled != null) {
				activate = batusiEnabled;
			}

			if (activate) {
				int defNumBats = getPlugin().getVampireConfig().vampire.batusi.numberOfBats;
				int maxNumBats = getPlugin().getVampireConfig().vampire.batusi.maxBats;
				if (defNumBats < 0) {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.BATUSI_DEFVALUE_ERROR);
					defNumBats = 0;
				}
				if (maxNumBats < 0) {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.BATUSI_DEFVALUE_ERROR);
					maxNumBats = defNumBats;
				}
				numBats = numberOfBats;
				if (numBats > maxNumBats) {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.BATUSI_TOO_MANY,
							"{default_bats}", Integer.toString(maxNumBats));
					numBats = maxNumBats;
				}
			}
			else if (specifiedNum) {
				getPlugin().sendMessage(sender,
						MessageType.INFO,
						CommandMessageKeys.BATUSI_IGNORED_BATS);
			}

			vPlayer.setBatusi(activate, numBats);
			return Command.SINGLE_SUCCESS;
		} else {
			Component nosferatuType = getPlugin().getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
			Component batusiAction = getPlugin().getMessage(sender, GrammarMessageKeys.BATUSI);
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
					new Tuple<>("{vampire_type}", nosferatuType),
					new Tuple<>("{action}", batusiAction));
		}
		return 0;
	}

	public static final LiteralCommandNode<CommandSourceStack> holywater = Commands
			.literal("holywater")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_GIVE_HOLYWATER.getPermissionNode()))
			.executes(CmdVampire::holywaterCommand)
			.then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
					.executes(CmdVampire::holywaterCommand))
			.build();
	public static final CommandMessageKeys holywaterDesc = CommandMessageKeys.HOLYWATER_DESCRIPTION;
	public static int holywaterCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		int quantity = 1;
		try {
			quantity = ctx.getArgument("amount", int.class);
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		ItemStack water = getHolyWaterUtil().createHolyWater(player);
		water.setAmount(quantity);
		Map<Integer, ItemStack> result = player.getInventory().addItem(water);
		if (!result.isEmpty()) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.FULL_INVENTORY);
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> ring = Commands
			.literal("ring")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_GIVE_RING.getPermissionNode()))
			.executes(CmdVampire::ringCommand)
			.build();
	public static final CommandMessageKeys ringDesc = CommandMessageKeys.RING_DESCRIPTION;
	public static int ringCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		if (!(executor instanceof Player player))
			throw EXECUTOR_NOT_PLAYER.create();

		ItemStack ring = getRingUtil().getSunRing();
		Map<Integer, ItemStack> result = player.getInventory().addItem(ring);
		if (!result.isEmpty()) {
			getPlugin().sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.FULL_INVENTORY);
			return 0;
		}
		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> setVampire = Commands
			.literal("vampire")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_VAMPIRE.getPermissionNode()))
			.then(Commands.argument("isVampire", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::setVampireCommand)
					.then(Commands.argument("targets", ArgumentTypes.playerProfiles())
							.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_VAMPIRE_OTHERS.getPermissionNode()))
							.executes(CmdVampire::setVampireCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> setV = Commands
			.literal("v")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_VAMPIRE.getPermissionNode()))
			.redirect(setVampire)
			.build();
	public static final CommandMessageKeys setVampireDesc = CommandMessageKeys.SET_VAMPIRE_DESCRIPTION;
	public static int setVampireCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		boolean isVampire = ctx.getArgument("isVampire", boolean.class);

		Collection<PlayerProfile> profiles = null;
		try {
			profiles = ctx.getArgument("targets", PlayerProfileListResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (profiles == null) {
			if (executor instanceof Player p)
				profiles = List.of(p.getPlayerProfile());
			else
				throw EXECUTOR_NOT_PLAYER_OPTIONAL.create();
		}
		if (profiles.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (PlayerProfile profile : profiles) {
			if (profile.getId() == null)
				continue;
			OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getId());

			VPlayer vPlayer = getPlugin().getVPlayer(target);
			if (vPlayer == null) {
				getPlugin().sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
				continue;
			}
			if (vPlayer.isVampire() != isVampire) {
				Component displayName = Component.text(profile.getName());
				if (!isVampire || !getPlugin().getWerewolvesCompat().isWerewolf(profile.getId())) {
					vPlayer.setReason(InfectionReason.OPERATOR);
					vPlayer.setMaker(null);
					vPlayer.setVampire(isVampire);

					vPlayer.update();

					Component onOff = isVampire ? getPlugin().getMessage(sender, GrammarMessageKeys.ON) : getPlugin().getMessage(sender, GrammarMessageKeys.OFF);
					Component attributeName = getPlugin().getMessage(sender, CommandMessageKeys.SET_VAMPIRE_ATTRIBUTE);
					getPlugin().sendMessage(sender,
							MessageType.INFO,
							CommandMessageKeys.SET_CHANGED_VALUE,
							new Tuple<>("{player}", displayName),
							new Tuple<>("{attribute}", attributeName),
							new Tuple<>("{value}", onOff));
				} else {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.SET_ERROR_HYBRID,
							new Tuple<>("{player}", displayName));
					continue;
				}
			}
			result += Command.SINGLE_SUCCESS;
		}

		if (result != 1)
			sendMessage(sender, executor, "Set vampire for " + result + " players.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> setNosferatu = Commands
			.literal("nosferatu")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_NOSFERATU.getPermissionNode()))
			.then(Commands.argument("isNosferatu", YesNoArgumentType.yesNoClass())
					.executes(CmdVampire::setNosferatuCommand)
					.then(Commands.argument("targets", ArgumentTypes.playerProfiles())
							.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_NOSFERATU_OTHERS.getPermissionNode()))
							.executes(CmdVampire::setNosferatuCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> setN = Commands
			.literal("n")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_NOSFERATU.getPermissionNode()))
			.redirect(setNosferatu)
			.build();
	public static final CommandMessageKeys setNosferatuDesc = CommandMessageKeys.SET_NOSFERATU_DESCRIPTION;
	public static int setNosferatuCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		boolean isNosferatu = ctx.getArgument("isNosferatu", boolean.class);

		Collection<PlayerProfile> profiles = null;
		try {
			profiles = ctx.getArgument("targets", PlayerProfileListResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (profiles == null) {
			if (executor instanceof Player p)
				profiles = List.of(p.getPlayerProfile());
			else
				throw EXECUTOR_NOT_PLAYER_OPTIONAL.create();
		}
		if (profiles.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (PlayerProfile profile : profiles) {
			if (profile.getId() == null)
				continue;
			OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getId());

			VPlayer vPlayer = getPlugin().getVPlayer(target);
			if (vPlayer == null) {
				getPlugin().sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
				continue;
			}
			if (vPlayer.isVampire() != isNosferatu) {
				Component displayName = Component.text(profile.getName() == null ? "???" : profile.getName());
				if (!isNosferatu || !getPlugin().getWerewolvesCompat().isWerewolf(profile.getId())) {
					if (isNosferatu && !vPlayer.isVampire()) {
						vPlayer.setReason(InfectionReason.OPERATOR);
						vPlayer.setMaker(null);
						vPlayer.setVampire(true);

						vPlayer.update();
					}
					if (vPlayer.isNosferatu() != isNosferatu) {
						vPlayer.setNosferatu(isNosferatu);

						vPlayer.update();
					}

					Component onOff = isNosferatu ? getPlugin().getMessage(sender, GrammarMessageKeys.ON) : getPlugin().getMessage(sender, GrammarMessageKeys.OFF);
					Component attributeName = getPlugin().getMessage(sender, CommandMessageKeys.SET_NOSFERATU_ATTRIBUTE);
					getPlugin().sendMessage(sender,
							MessageType.INFO,
							CommandMessageKeys.SET_CHANGED_VALUE,
							new Tuple<>("{player}", displayName),
							new Tuple<>("{attribute}", attributeName),
							new Tuple<>("{value}", onOff));
				}
				else {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.SET_ERROR_HYBRID,
							new Tuple<>("{player}", displayName));
					continue;
				}
			}
			result += Command.SINGLE_SUCCESS;
		}

		if (result != 1)
			sendMessage(sender, executor, "Set nosferatu for " + result + " players.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> setInfection = Commands
			.literal("infection")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_INFECTION.getPermissionNode()))
			.then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
					.executes(CmdVampire::setInfectionCommand)
					.then(Commands.argument("targets", ArgumentTypes.playerProfiles())
							.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_INFECTION_OTHERS.getPermissionNode()))
							.executes(CmdVampire::setInfectionCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> setI = Commands
			.literal("i")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_INFECTION.getPermissionNode()))
			.redirect(setInfection)
			.build();
	public static final CommandMessageKeys setInfectionDesc = CommandMessageKeys.SET_INFECTION_DESCRIPTION;
	public static int setInfectionCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		double value = ctx.getArgument("value", double.class);

		Collection<PlayerProfile> profiles = null;
		try {
			profiles = ctx.getArgument("targets", PlayerProfileListResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (profiles == null) {
			if (executor instanceof Player p)
				profiles = List.of(p.getPlayerProfile());
			else
				throw EXECUTOR_NOT_PLAYER_OPTIONAL.create();
		}
		if (profiles.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (PlayerProfile profile : profiles) {
			if (profile.getName() == null)
				continue;
			OfflinePlayer target = Bukkit.getOfflinePlayer(profile.getName());

			VPlayer vPlayer = getPlugin().getVPlayer(target);
			if (vPlayer == null) {
				getPlugin().sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
				continue;
			}
			Component displayName = Component.text(profile.getName());
			if (vPlayer.isVampire()) {
				getPlugin().sendMessage(sender,
						MessageType.ERROR,
						VampirismMessageKeys.ALREADY_VAMPIRE,
						new Tuple<>("{player}", displayName));
				continue;
			} else {
				if (!getPlugin().getWerewolvesCompat().isWerewolf(profile.getId())) {
					InfectionReason reason = vPlayer.getReason();
					UUID makerUUID = vPlayer.getMakerUUID();
					if (reason == null) {
						reason = InfectionReason.OPERATOR;
						makerUUID = null;
					}
					vPlayer.setInfection(0);
					vPlayer.addInfection(value / 100, reason, makerUUID);
					vPlayer.update();

					Component attributeName = getPlugin().getMessage(sender, CommandMessageKeys.SET_INFECTION_ATTRIBUTE);
					Component valComp = Component.text(String.format("%.2f%%", value));
					getPlugin().sendMessage(sender,
							MessageType.INFO,
							CommandMessageKeys.SET_CHANGED_VALUE,
							new Tuple<>("{player}", displayName),
							new Tuple<>("{attribute}", attributeName),
							new Tuple<>("{value}", valComp));
				} else {
					getPlugin().sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.SET_ERROR_HYBRID,
							new Tuple<>("{player}", displayName));
					continue;
				}
			}
			result += Command.SINGLE_SUCCESS;
		}

		if (result != 1)
			sendMessage(sender, executor, "Set infection level for " + result + " players.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> setFood = Commands
			.literal("food")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_FOOD.getPermissionNode()))
			.then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
					.executes(CmdVampire::setFoodCommand)
					.then(Commands.argument("targets", ArgumentTypes.players())
							.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_FOOD_OTHERS.getPermissionNode()))
							.executes(CmdVampire::setFoodCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> setF = Commands
			.literal("f")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_FOOD.getPermissionNode()))
			.redirect(setFood)
			.build();
	public static final CommandMessageKeys setFoodDesc = CommandMessageKeys.SET_FOOD_DESCRIPTION;
	public static int setFoodCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		int value = ctx.getArgument("value", int.class);

		List<Player> targets = null;
		try {
			targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (targets == null) {
			if (executor instanceof Player p)
				targets = List.of(p);
			else
				throw EXECUTOR_NOT_PLAYER_OPTIONAL.create();
		}
		if (targets.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (Player target : targets) {
			target.setFoodLevel(value);

			Component attributeName = getPlugin().getMessage(sender, CommandMessageKeys.SET_FOOD_ATTRIBUTE);
			Component displayName = target.displayName();
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					new Tuple<>("{player}", displayName),
					new Tuple<>("{attribute}", attributeName),
					new Tuple<>("{value}", Component.text(String.format("%d", value))));
			result += Command.SINGLE_SUCCESS;
		}

		if (result != 1)
			sendMessage(sender, executor, "Set food for " + result + " players.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> setHealth = Commands
			.literal("health")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_HEALTH.getPermissionNode()))
			.then(Commands.argument("value", DoubleArgumentType.doubleArg(0))
					.executes(CmdVampire::setHealthCommand)
					.then(Commands.argument("targets", ArgumentTypes.entities())
							.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_HEALTH_OTHERS.getPermissionNode()))
							.executes(CmdVampire::setHealthCommand)))
			.build();
	public static final LiteralCommandNode<CommandSourceStack> setH = Commands
			.literal("h")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_HEALTH.getPermissionNode()))
			.redirect(setHealth)
			.build();
	public static final CommandMessageKeys setHealthDesc = CommandMessageKeys.SET_HEALTH_DESCRIPTION;
	public static int setHealthCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Entity executor = ctx.getSource().getExecutor();

		double value = ctx.getArgument("value", double.class);

		List<Entity> targets = null;
		try {
			targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
					.resolve(ctx.getSource());
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		if (targets == null) {
			if (executor instanceof LivingEntity p)
				targets = List.of(p);
			else
				throw EXECUTOR_NOT_LIVING_OPTIONAL.create();
		}
		if (targets.isEmpty()) {
			sendRichMessage(sender, executor, "<red>No targets found!");
			return 0;
		}

		int result = 0;
		for (Entity rawTarget : targets) {
			if (!(rawTarget instanceof LivingEntity target))
				continue;
			AttributeInstance maxHealthAttr = target.getAttribute(getPlugin().getVersionCompat().HEALTH_ATTR);
			double maxHealth = 20;
			if (maxHealthAttr != null)
				maxHealth = maxHealthAttr.getValue();
			double newHealth = MathUtil.limitNumber(value, 0D, maxHealth);
			target.setHealth(newHealth);

			Component attributeName = getPlugin().getMessage(sender, CommandMessageKeys.SET_HEALTH_ATTRIBUTE);
			Component displayName = target instanceof Player p ? p.displayName() : target.name();
			getPlugin().sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					new Tuple<>("{player}", displayName),
					new Tuple<>("{attribute}", attributeName),
					new Tuple<>("{value}", Component.text(String.format("%f", newHealth))));
			result += Command.SINGLE_SUCCESS;
		}

		if (result != 1)
			sendMessage(sender, executor, "Set health for " + result + " entities.");

		return result;
	}

	public static final LiteralCommandNode<CommandSourceStack> setHelp = Commands
			.literal("help")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET_HELP.getPermissionNode()))
			.executes(CmdVampire::setHelpCommand)
			.then(Commands.argument("subcommand", StringArgumentType.word())
					.executes(CmdVampire::setHelpCommand))
			.build();
	public static final CommandMessageKeys setHelpDesc = CommandMessageKeys.SET_HELP_DESCRIPTION;;
	public static int setHelpCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Entity executor = ctx.getSource().getExecutor();
		CommandSender sender = executor != null ? executor : ctx.getSource().getSender();

		Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider> commandInfo = setData;
		String subcommand = "";
		try {
			subcommand = ctx.getArgument("subcommand", String.class);
			commandInfo = setCommandData.get(subcommand);
			if (commandInfo == null)
				throw SUBCOMMAND_DOESNT_EXIST.create(subcommand);
			subcommand += " ";
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		VampireRevamp.getInstance().sendMessage(sender, MessageType.HELP, commandInfo.y);
		Map<CommandNode<CommandSourceStack>, String> usages = VampireRevamp.getInstance().getDispatcher()
				.getSmartUsage(commandInfo.x, ctx.getSource());
		String finalSubcommand = subcommand;
		usages.forEach((node, usage) ->
				sendMessage(sender, executor, "/vampire set " + finalSubcommand + usage)
		);

		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> set = Commands
			.literal("set")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_SET.getPermissionNode()))
			.then(setVampire)
			.then(setV)
			.then(setNosferatu)
			.then(setN)
			.then(setInfection)
			.then(setI)
			.then(setFood)
			.then(setF)
			.then(setHealth)
			.then(setH)
			.then(setHelp)
			.build();
	public static final CommandMessageKeys setDesc = CommandMessageKeys.SET_DESCRIPTION;

	public static final LiteralCommandNode<CommandSourceStack> help = Commands
			.literal("help")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND_HELP.getPermissionNode()))
			.executes(CmdVampire::helpCommand)
			.then(Commands.argument("subcommand", StringArgumentType.word())
					.executes(CmdVampire::helpCommand))
			.build();
	public static final CommandMessageKeys helpDesc = CommandMessageKeys.HELP_DESCRIPTION;;
	public static int helpCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Entity executor = ctx.getSource().getExecutor();
		CommandSender sender = executor != null ? executor : ctx.getSource().getSender();

		Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider> commandInfo = baseData;
		String subcommand = "";
		try {
			subcommand = ctx.getArgument("subcommand", String.class);
			commandInfo = commandData.get(subcommand);
			if (commandInfo == null)
				throw SUBCOMMAND_DOESNT_EXIST.create(subcommand);
			subcommand += " ";
		}
		catch (IllegalArgumentException ex) {
			if (!ex.getMessage().startsWith("No such argument"))
				throw ex;
		}

		VampireRevamp.getInstance().sendMessage(sender, MessageType.HELP, commandInfo.y);
		Map<CommandNode<CommandSourceStack>, String> usages = VampireRevamp.getInstance().getDispatcher()
				.getSmartUsage(commandInfo.x, ctx.getSource());
		String finalSubcommand = subcommand;
		usages.forEach((node, usage) ->
				sendMessage(sender, executor, "/vampire " + finalSubcommand + usage)
		);

		return Command.SINGLE_SUCCESS;
	}

	public static final LiteralCommandNode<CommandSourceStack> VAMPIRE_COMMAND = Commands
			.literal("vampire")
			.requires(sender -> sender.getSender().hasPermission(Perm.COMMAND.getPermissionNode()))
			.then(reload)
			.then(show)
			.then(bloodlust)
			.then(modebloodlust)
			.then(b)
			.then(intend)
			.then(modeintend)
			.then(i)
			.then(nightvision)
			.then(modenightvision)
			.then(nv)
			.then(offer)
			.then(o)
			.then(accept)
			.then(a)
			.then(flask)
			.then(f)
			.then(shriek)
			.then(list)
			.then(version)
			.then(v)
			.then(batusi)
			.then(bat)
			.then(holywater)
			.then(ring)
			.then(set)
			.then(help)
			.build();
	private static final Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider> baseData = new Tuple<>(
			VAMPIRE_COMMAND,
			CommandMessageKeys.BASE_DESCRIPTION
	);
	private static final Map<String, Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider>> commandData = new HashMap<>(VAMPIRE_COMMAND.getChildren().size() + 1);
	static {
		commandData.put("reload", new Tuple<>(reload, reloadDesc));
		commandData.put("show", new Tuple<>(show, showDesc));
		commandData.put("bloodlust", new Tuple<>(bloodlust, bloodlustDesc));
		commandData.put("modebloodlust", new Tuple<>(bloodlust, bloodlustDesc));
		commandData.put("b", new Tuple<>(bloodlust, bloodlustDesc));
		commandData.put("intend", new Tuple<>(intend, intendDesc));
		commandData.put("modeintend", new Tuple<>(intend, intendDesc));
		commandData.put("i", new Tuple<>(intend, intendDesc));
		commandData.put("nightvision", new Tuple<>(nightvision, nightvisionDesc));
		commandData.put("modenightvision", new Tuple<>(nightvision, nightvisionDesc));
		commandData.put("nv", new Tuple<>(nightvision, nightvisionDesc));
		commandData.put("offer", new Tuple<>(offer, offerDesc));
		commandData.put("o", new Tuple<>(offer, offerDesc));
		commandData.put("accept", new Tuple<>(accept, acceptDesc));
		commandData.put("a", new Tuple<>(accept, acceptDesc));
		commandData.put("flask", new Tuple<>(flask, flaskDesc));
		commandData.put("f", new Tuple<>(flask, flaskDesc));
		commandData.put("shriek", new Tuple<>(shriek, shriekDesc));
		commandData.put("list", new Tuple<>(list, listDesc));
		commandData.put("version", new Tuple<>(version, versionDesc));
		commandData.put("v", new Tuple<>(version, versionDesc));
		commandData.put("batusi", new Tuple<>(batusi, batusiDesc));
		commandData.put("bat", new Tuple<>(batusi, batusiDesc));
		commandData.put("holywater", new Tuple<>(holywater, holywaterDesc));
		commandData.put("ring", new Tuple<>(ring, ringDesc));
		commandData.put("set", new Tuple<>(set, setDesc));
		commandData.put("help", new Tuple<>(help, helpDesc));
	}
	private static final Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider> setData = new Tuple<>(
			set,
			CommandMessageKeys.SET_DESCRIPTION
	);
	private static final Map<String, Tuple<LiteralCommandNode<CommandSourceStack>, KeyProvider>> setCommandData = new HashMap<>(VAMPIRE_COMMAND.getChildren().size() + 1);
	static {
		setCommandData.put("vampire", new Tuple<>(setVampire, setVampireDesc));
		setCommandData.put("v", new Tuple<>(setVampire, setVampireDesc));
		setCommandData.put("nosferatu", new Tuple<>(setNosferatu, setNosferatuDesc));
		setCommandData.put("n", new Tuple<>(setNosferatu, setNosferatuDesc));
		setCommandData.put("infection", new Tuple<>(setInfection, setInfectionDesc));
		setCommandData.put("i", new Tuple<>(setInfection, setInfectionDesc));
		setCommandData.put("food", new Tuple<>(setFood, setFoodDesc));
		setCommandData.put("f", new Tuple<>(setFood, setFoodDesc));
		setCommandData.put("health", new Tuple<>(setHealth, setHealthDesc));
		setCommandData.put("h", new Tuple<>(setHealth, setHealthDesc));
		setCommandData.put("help", new Tuple<>(setHelp, setHelpDesc));
	}
}
