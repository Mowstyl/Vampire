package com.clanjhoo.vampire.cmd;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import com.clanjhoo.vampire.keyproviders.SkillMessageKeys;
import com.clanjhoo.vampire.keyproviders.VampirismMessageKeys;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

@CommandAlias("v|vampire")
@CommandPermission("vampire.basecommand")
public class CmdVampire extends BaseCommand {
	private Map<String, RegisteredCommand> commandMap;

	public CmdVampire() {
		commandMap = new HashMap<>();
	}

	public void initialize() {
		initializeMap();
	}

	private void initializeMap() {
		RootCommand vampireCommand = VampireRevamp.getCommandManager().getRegisteredRootCommands().iterator().next();

		for (Entry<String, RegisteredCommand> entry : vampireCommand.getSubCommands().entries()) {
			RegisteredCommand command = entry.getValue();
			String commandName = command.getCommand();
			if (commandName.length() > 2) {
				commandName = commandName.substring(2);
				if (!commandMap.containsKey(commandName)) {
					commandMap.put(commandName, command);
					VampireRevamp.debugLog(Level.INFO, "Key: " + commandName);
				}
			}
		}
	}

	@HelpCommand
	@Subcommand("help|h|?")
	@CommandCompletion("@range:1-2")
	@Description("{@@commands.help.description}")
	@Syntax("[page=1]")
	public void onHelp(CommandSender sender, CommandHelp help) {
		if (help.getSearch() == null || help.getSearch().isEmpty() || !help.getSearch().get(0).equalsIgnoreCase("set")) {
			int maxPages = 2;

			sender.spigot().sendMessage(TextUtil.getHelpHeader(help, maxPages, help.getCommandName(), sender));

			if (help.getPage() == 1) {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("help", commandMap.get("help"), sender,  0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("show", commandMap.get("show"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("bloodlust", commandMap.get("bloodlust"), sender, 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("intend", commandMap.get("intend"), sender, 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("nightvision", commandMap.get("nightvision"), sender, 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("offer", commandMap.get("offer"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("accept", commandMap.get("accept"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("flask", commandMap.get("flask"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("shriek", commandMap.get("shriek"), sender, 0));
			} else {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("batusi", commandMap.get("batusi"), sender, 2));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("list", commandMap.get("list"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("set", commandMap.get("set"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("version", commandMap.get("version"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("reload", commandMap.get("reload"), sender, 0));
			}
		}
		else {
			(new CmdVampireSet()).onHelp(sender, help);
		}
	}

	@Subcommand("reload")
	@CommandCompletion("@reloads")
	@CommandPermission("vampire.reload")
	@Description("{@@commands.reload_description}")
	@Syntax("[locales/config/all]")
	public void onReload(CommandSender sender, @Optional String reloadType) {
		boolean result = false;
		boolean printResult = true;
		if (reloadType == null || reloadType.equalsIgnoreCase("all"))
			result = VampireRevamp.getInstance().reloadAll();
		else if (reloadType.equalsIgnoreCase("locales"))
			result = VampireRevamp.getInstance().reloadLocales();
		else if (reloadType.equalsIgnoreCase("config"))
			result = VampireRevamp.getInstance().reloadVampireConfig();
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", reloadType);
			sender.sendMessage(getCommandSyntax("reload"));
			printResult = false;
		}

		if (printResult) {
			if (result)
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.RELOAD_SUCCESS);
			else
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.RELOAD_FAIL);
		}
	}

	@Subcommand("show")
	@CommandCompletion("@players")
	@CommandPermission("vampire.show")
	@Description("{@@commands.show_description}")
	@Syntax("[player=you]")
	public void onShow(CommandSender sender, @Optional String targetName) {
		if (sender instanceof Player || targetName != null) {
			Player player;
			if (targetName == null) {
				player = (Player) sender;
			}
			else {
				player = Bukkit.getPlayer(targetName);
			}
			if (player != null) {
				PluginConfig conf = VampireRevamp.getVampireConfig();
				boolean self = sender instanceof Player && ((Player) sender).getUniqueId().equals(player.getUniqueId());

				// Test permissions
				if (self || Perm.SHOW_OTHER.has(sender, true)) {
					boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{player.getUniqueId()}, (uplayer) -> {
						String[] youAreWere = VampireRevamp.getYouAreWere(sender, player, self);
						String you = youAreWere[0];
						String are = youAreWere[1];
						String were = youAreWere[2];
						String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
						String on = VampireRevamp.getMessage(sender, GrammarMessageKeys.ON);
						String off = VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);

						sender.spigot().sendMessage(TextUtil.getPlayerInfoHeader(uplayer.isVampire(),
								uplayer.isNosferatu(),
								player.getDisplayName(),
								sender));
						if (uplayer.isVampire()) {
							if (uplayer.isNosferatu())
								vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_TYPE,
									"{player}", you,
									"{to_be}", are,
									"{vampire_type}", vampireType);

							InfectionReason reason = uplayer.getReason();
							String parent = null;
							if (reason.isMaker()) {
								parent = uplayer.getMakerName();
							}
							if (parent == null || parent.isEmpty()) {
								parent = "someone";
							}
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									reason.getDescKey(),
									"{player}", you,
									"{to_be_past}", were,
									"{parent}", parent);

							String bloodlustName = VampireRevamp.getMessage(sender, GrammarMessageKeys.BLOODLUST);
							bloodlustName = bloodlustName.substring(0, 1).toUpperCase() + bloodlustName.substring(1);
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_BLOODLUST,
									"{bloodlust}", bloodlustName,
									"{enabled}", uplayer.isBloodlusting() ? on : off,
									"{percent}",String.format("%.1f%%", uplayer.combatDamageFactor() * 100));

							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_INTENT,
									"{enabled}", uplayer.isIntending() ? on : off,
									"{percent}", String.format("%.1f%%", uplayer.combatInfectRisk() * 100));

							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_NIGHTVISION,
									"{enabled}", uplayer.isUsingNightVision() ? on : off);

							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_TEMPERATURE,
									"{percent}", String.format("%d%%", (int) Math.round(uplayer.getTemp() * 100)));

							int rad = (int) Math.round(100 * uplayer.getRad());
							int sun = (int) Math.round(100 * SunUtil.calcSolarRad(player.getWorld(), player));
							double terrain = 1d - SunUtil.calcTerrainOpacity(player.getLocation().getBlock());
							double armor = 1d - SunUtil.calcArmorOpacity(player);
							int base = (int) Math.round(100 * conf.radiation.baseRadiation);

							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_RADIATION_KEYS);

							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_RADIATION_VALUES,
									"{rads}", String.format("%+d%%", rad),
									"{sun}", String.format("%d", sun),
									"{terrain}", String.format("%.2f", terrain),
									"{armor}", String.format("%.2f", armor),
									"{base}", String.format("%+d", base));
						}
						else if (uplayer.isInfected()) {
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_INFECTED,
									"{player}", you,
									"{to_be}", are,
									"{percent}", String.format("%d%%", Math.round(uplayer.getInfection())));

							InfectionReason reason = uplayer.getReason();
							String parent = null;
							if (reason.isMaker()) {
								parent = uplayer.getMakerName();
							}
							if (parent == null || parent.isEmpty()) {
								parent = "someone";
							}
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									reason.getDescKey(),
									"{player}", you,
									"{to_be_past}", were,
									"{parent}", parent);
						}
						else {
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SHOW_CURED,
									"{player}", you,
									"{to_be}", are,
									"{vampire_type}", vampireType);
						}
					}, () -> {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.DATA_NOT_FOUND);
					}, true);
					if (!success) {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.DATA_NOT_FOUND);
					}
				}
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.INFO,
						CommandMessageKeys.NO_PLAYER_FOUND,
						"{player}", targetName);
			}
		}
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.NOT_ENOUGH_INPUTS);
			sender.sendMessage(getCommandSyntax("show"));
		}
	}

	@Subcommand("bloodlust|modebloodlust|b")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.bloodlust")
	@Description("{@@commands.bloodlust_description}")
	@Syntax("[yes/no=toggle]")
	public void onModeBloodlust(Player sender, @Optional String yesno) {
		VampireRevamp.debugLog(Level.INFO, "Executed command!");
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.debugLog(Level.INFO, "Blacklisted world!");
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			VampireRevamp.debugLog(Level.INFO, "Scheduling synchronous");
			boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (uplayer) -> {
				VampireRevamp.debugLog(Level.INFO, "Acceptance");
				if (uplayer.isVampire()) {
					VampireRevamp.debugLog(Level.INFO, "Le vampire");
					boolean isActive = uplayer.isBloodlusting();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setBloodlusting(!isActive);
				} else {
					VampireRevamp.debugLog(Level.INFO, "Non non non!");
					String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
					String bloodlustAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.BLOODLUST);
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
							"{vampire_type}", vampireType,
							"{action}", bloodlustAction);
				}
			}, () -> {
				VampireRevamp.debugLog(Level.INFO, "Errorance");
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}, true);
			if (!success) {
				VampireRevamp.debugLog(Level.INFO, "Scheduling failed!");
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}
		}
		else {
			VampireRevamp.debugLog(Level.INFO, "Bad Syntax!");
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", yesno);
			sender.sendMessage(getCommandSyntax("bloodlust"));
		}
		VampireRevamp.debugLog(Level.INFO, "End of ZA WARUDO");
	}

	@Subcommand("intend|modeintend|i")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.intent")
	@Description("{@@commands.intent_description}")
	@Syntax("[yes/no=toggle]")
	public void onModeIntend(Player sender, @Optional String yesno) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (uplayer) -> {
				if (uplayer.isVampire()) {
					boolean isActive = uplayer.isIntending();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setIntending(!isActive);
				} else {
					String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
					String intentAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.INTEND);
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
							"{vampire_type}", vampireType,
							"{action}", intentAction);
				}
			}, () -> {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}, true);
			if (!success) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}
		}
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", yesno);
			sender.sendMessage(getCommandSyntax("intend"));
		}
	}

	@Subcommand("nightvision|modenightvision|nv")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.nightvision")
	@Description("{@@commands.nightvision_description}")
	@Syntax("[yes/no=toggle]")
	public void onModeNightvision(Player sender, @Optional String yesno) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (uplayer) -> {
				if (uplayer.isVampire()) {
					boolean isActive = uplayer.isUsingNightVision();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setUsingNightVision(!isActive);
				} else {
					String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
					String nvAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.NIGHTVISION);
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
							"{vampire_type}", vampireType,
							"{action}", nvAction);
				}
			}, () -> {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}, true);
			if (!success) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}
		}
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", yesno);
			sender.sendMessage(getCommandSyntax("nightvision"));
		}
	}

	@Subcommand("offer|o")
	@CommandCompletion("@players @range:1-20")
	@CommandPermission("vampire.trade.offer")
	@Description("{@@commands.offer_description}")
	@Syntax("<player> [amount=4.0]")
	public void onOffer(Player sender, String targetName, @Default("4") double rawamount) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		Player pyou = Bukkit.getPlayer(targetName);

		if (pyou != null) {
			double amount = MathUtil.limitNumber(rawamount, 0D, 20D);
			if (amount != rawamount) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.OFFER_INVALID_VALUE);
				return;
			}

			boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (vme) -> {
				boolean success2 = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{pyou.getUniqueId()},
					(vyou) -> vme.tradeOffer(sender, vyou, amount),
					() -> VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND),
					true);
				if (!success2) {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND);
				}
			}, () -> {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}, true);
			if (!success) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}
		}
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NO_PLAYER_FOUND);
		}
	}

	@Subcommand("accept|a")
	@CommandPermission("vampire.trade.accept")
	@Description("{@@commands.nightvision_description}")
	public void onAccept(Player sender) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()},
			UPlayer::tradeAccept,
			() -> VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND),
			true);
		if (!success) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
		}
	}

	@Subcommand("flask|f")
	@CommandCompletion("@range:1-20")
	@CommandPermission("vampire.flask")
	@Description("{@@commands.flask_description}")
	@Syntax("[amount=4.0]")
	public void onFlask(Player sender, @Default("4") Integer amount) {
		if (amount <= 0) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", amount.toString());
			return;
		}
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (vme) -> {
			// Does the player have the required amount?
			//if ((vme.isVampire() && amount > vme.getFood()) || (!vme.isVampire() && amount > sender.getHealth())) {
			if (amount > sender.getHealth()) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						SkillMessageKeys.FLASK_INSUFFICIENT);
			} else {
				// ... create a blood flask!
				if (BloodFlaskUtil.fillBottle(vme, amount)) {
					/*
					if (vme.isVampire()) {
						vme.addFood(-amount);
					} else {
						sender.setHealth(sender.getHealth() - amount);
					}
					 */
					sender.setHealth(sender.getHealth() - amount);
					// Inform
					VampireRevamp.sendMessage(sender,
							MessageType.INFO,
							SkillMessageKeys.FLASK_SUCCESS);
				}
				else {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							SkillMessageKeys.FLASK_NO_BOTTLE);
				}
			}
		}, () -> {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
		}, true);
		if (!success) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
		}
	}

	@Subcommand("shriek")
	@CommandPermission("vampire.shriek")
	@Description("{@@commands.shriek_description}")
	public void onShriek(Player sender) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (vme) -> {
			if (vme.isVampire()) {
				vme.shriek();
			} else {
				String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
				String shriekAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.SHRIEK);
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
						"{vampire_type}", vampireType,
						"{action}", shriekAction);
			}
		}, () -> {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
		}, true);
		if (!success) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.DATA_NOT_FOUND);
		}
	}

	@Subcommand("list")
	@CommandCompletion("@range:1-5")
	@CommandPermission("vampire.list")
	@Description("{@@commands.list_description}")
	@Syntax("[page=1]")
	public void onList(CommandSender sender, @Default("1") int page) {
		List<String> vampiresOnline = new ArrayList<>();
		// List<String> vampiresOffline = new ArrayList<>();
		List<String> infectedOnline = new ArrayList<>();
		// List<String> infectedOffline = new ArrayList<>();

		/*
		for (UPlayer uplayer : VampireRevamp.getInstance().uPlayerColl.getAll()) {
			if (uplayer.getOfflinePlayer() != null) {
				OfflinePlayer p = uplayer.getOfflinePlayer();
				if (uplayer.isVampire()) {
					if (p.isOnline()) {
						vampiresOnline.add(ChatColor.WHITE.toString() + p.getName());
					}
					else {
						vampiresOffline.add(ChatColor.WHITE.toString() +  p.getName());
					}
				} else if (uplayer.isInfected()) {
					if (p.isOnline()) {
						infectedOnline.add(ChatColor.WHITE.toString() +  p.getName());
					}
					else {
						infectedOffline.add(ChatColor.WHITE.toString() +  p.getName());
					}
				}
			}
		}
		 */

		for (Player player : Bukkit.getOnlinePlayers()) {
			UPlayer uplayer = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{player.getUniqueId()});
			if (uplayer.isVampire()) {
				vampiresOnline.add(uplayer.getPlayer().getName());
			}
			else if (uplayer.isInfected()) {
				infectedOnline.add(uplayer.getPlayer().getName());
			}
		}

		// Create Messages
		List<String> lines = new ArrayList<>();

		String vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
		vampireType = ("" + vampireType.charAt(0)).toUpperCase() + vampireType.substring(1);
		String infectedType = VampireRevamp.getMessage(sender, GrammarMessageKeys.INFECTED_TYPE);
		infectedType = ("" + infectedType.charAt(0)).toUpperCase() + infectedType.substring(1);
		String onlineStr = VampireRevamp.getMessage(sender, GrammarMessageKeys.ONLINE);
		//String offlineStr = VampireRevamp.getMessage(sender, GrammarMessageKeys.OFFLINE);

		if (vampiresOnline.size() > 0)
		{
			lines.add(ChatColor.LIGHT_PURPLE + "=== " + vampireType + "s " + onlineStr + " ===");
			//lines.add(TextUtil.implodeCommaAndDot(vampiresOnline, "<i>"));
			lines.add(ChatColor.YELLOW + String.join(", " + ChatColor.YELLOW, vampiresOnline));
		}

		/*
		if (vampiresOffline.size() > 0)
		{
			lines.add(ChatColor.LIGHT_PURPLE + "=== " + vampireType + "s " + offlineStr + " ===");
			//lines.add(TextUtil.implodeCommaAndDot(vampiresOffline, "<i>"));
			lines.add(ChatColor.YELLOW + String.join(", ", vampiresOffline));
		}
		 */

		if (infectedOnline.size() > 0)
		{
			lines.add(ChatColor.LIGHT_PURPLE + "=== " + infectedType + "s " + onlineStr + " ===");
			//lines.add(TextUtil.implodeCommaAndDot(infectedOnline, "<i>"));
			lines.add(ChatColor.YELLOW + String.join(", ", infectedOnline));
		}

		/*
		if (infectedOffline.size() > 0)
		{
			lines.add(ChatColor.LIGHT_PURPLE + "=== " + infectedType + "s " + offlineStr + " ===");
			//lines.add(TextUtil.implodeCommaAndDot(infectedOffline, "<i>"));
			lines.add(ChatColor.YELLOW + String.join(", ", infectedOffline));
		}
		 */

		// Send them
		lines = TextUtil.wrap(lines);
		for (String line : lines) {
			sender.sendMessage(line);
		}
	}

	@Subcommand("version|v")
	@CommandPermission("vampire.version")
	@Description("{@@commands.version_description}")
	public void onVersion(CommandSender sender) {
		List<BaseComponent[]> pd = TextUtil.getPluginDescription(VampireRevamp.getInstance());
		for (BaseComponent[] mess : pd) {
			sender.spigot().sendMessage(mess);
		}
	}

	@Subcommand("batusi|bat")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.batusi")
	@Description("{@@commands.batusi_description}")
	@Syntax("[yes/no=toggle]")
	public void onModeBatusi(Player sender, @Optional String yesno) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			VampireRevamp plugin = VampireRevamp.getInstance();
			boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{sender.getUniqueId()}, (uplayer) -> {
				if (uplayer.isNosferatu()) {
					boolean activate = !plugin.batEnabled.getOrDefault(sender.getUniqueId(), false);

					if (yesno != null) {
						activate = yesno.equals("yes");
					}

					uplayer.setBatusi(activate);
				} else {
					String nosferatuType = VampireRevamp.getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
					String batusiAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.BATUSI);
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
							"{vampire_type}", nosferatuType,
							"{action}", batusiAction);
				}
			}, () -> {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}, true);
			if (!success) {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND);
			}
		}
		else {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", yesno);
			sender.sendMessage(getCommandSyntax("batusi"));
		}
	}

	@Subcommand("holywater")
	@CommandPermission("vampire.give.holywater")
	@Description("{@@commands.holywater_description}")
	public void onHolyWater(Player sender, @Default("1") Integer quantity) {
		if (quantity < 1 || quantity > 64) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.NOT_VALID_VALUE,
					"{value}", quantity.toString());
			return;
		}
		ItemStack water = HolyWaterUtil.createHolyWater(sender);
		water.setAmount(quantity);
		Map<Integer, ItemStack> result = sender.getInventory().addItem(water);
		if (!result.isEmpty()) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.FULL_INVENTORY);
		}
	}

	@Subcommand("ring")
	@CommandPermission("vampire.give.ring")
	@Description("{@@commands.ring_description}")
	public void onRing(Player sender) {
		ItemStack ring = RingUtil.getSunRing();
		Map<Integer, ItemStack> result = sender.getInventory().addItem(ring);
		if (!result.isEmpty()) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.FULL_INVENTORY);
		}
	}

	@Subcommand("set")
	@CommandPermission("vampire.set")
	@Description("{@@commands.set_description}")
	public class CmdVampireSet extends BaseCommand {
		@Subcommand("help|h|?")
		public void onHelp(CommandSender sender, CommandHelp help) {
			int maxPages = 1;

			sender.spigot().sendMessage(TextUtil.getHelpHeader(help, maxPages, "set", sender));

			if (help.getPage() == 1) {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("vampire", commandMap.get("set vampire"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("nosferatu", commandMap.get("set nosferatu"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("infection", commandMap.get("set infection"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("food", commandMap.get("set food"), sender, 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("health", commandMap.get("set health"), sender, 0));
			}
		}

		@Subcommand("vampire|v")
		@CommandCompletion("@yesno @players")
		@Description("{@@commands.set_vampire_description}")
		@Syntax("<yes/no> [player=you]")
		public void onSetVampire(CommandSender sender, String yesno, @Optional String targetName) {
			if (yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
				boolean val = yesno.equalsIgnoreCase("yes");
				Perm perm = val ? Perm.SET_VAMPIRE_TRUE : Perm.SET_VAMPIRE_FALSE;
				if (perm.has(sender, true)) {
					Player player = null;
					if (targetName != null) {
						player = Bukkit.getPlayer(targetName);
					} else if (sender instanceof Player) {
						player = (Player) sender;
					} else {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.NOT_ENOUGH_INPUTS);
						sender.sendMessage(getCommandSyntax("set vampire"));
					}

					if (player != null) {
						Player finalPlayer = player;
						boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{player.getUniqueId()}, (uplayer) -> {
							if (uplayer.isVampire() != val) {
								if (!val || !VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
									uplayer.setReason(InfectionReason.OPERATOR);
									uplayer.setMaker(null);
									uplayer.setVampire(val);

									uplayer.update();

									String onOff = val ? VampireRevamp.getMessage(sender, GrammarMessageKeys.ON) : VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);
									String attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_VAMPIRE_ATTRIBUTE);
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SET_CHANGED_VALUE,
											"{player}", finalPlayer.getDisplayName(),
											"{attribute}", attributeName,
											"{value}", onOff);
								} else {
									VampireRevamp.sendMessage(sender,
											MessageType.ERROR,
											CommandMessageKeys.SET_ERROR_HYBRID,
											"{player}", finalPlayer.getDisplayName());
								}
							}
						}, () -> {
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									CommandMessageKeys.DATA_NOT_FOUND);
						}, true);
						if (!success) {
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									CommandMessageKeys.DATA_NOT_FOUND);
						}
					}
					else {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.NO_PLAYER_FOUND,
								"{player}", targetName);
					}
				}
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NOT_VALID_VALUE,
						"{value}", yesno);
				sender.sendMessage(getCommandSyntax("set vampire"));
			}
		}

		@Subcommand("nosferatu|n")
		@CommandCompletion("@yesno @players")
		@Description("{@@commands.set_nosferatu_description}")
		@Syntax("<yes/no> [player=you]")
		public void onSetNosferatu(CommandSender sender, String yesno, @Optional String targetName) {
			if (yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
				boolean val = yesno.equalsIgnoreCase("yes");
				Perm perm = val ? Perm.SET_NOSFERATU_TRUE : Perm.SET_NOSFERATU_FALSE;
				if (perm.has(sender, true)) {
					Player player = null;
					if (targetName != null) {
						player = Bukkit.getPlayer(targetName);
					} else if (sender instanceof Player) {
						player = (Player) sender;
					} else {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.NOT_ENOUGH_INPUTS);
						sender.sendMessage(getCommandSyntax("set nosferatu"));
					}

					if (player != null) {
						Player finalPlayer = player;
						boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{player.getUniqueId()}, (uplayer) -> {
							if (!val || !VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
								if (val && uplayer.isVampire() != val) {
									uplayer.setReason(InfectionReason.OPERATOR);
									uplayer.setMaker(null);
									uplayer.setVampire(val);

									uplayer.update();
								}
								if (uplayer.isNosferatu() != val) {
									uplayer.setNosferatu(val);

									uplayer.update();
								}

								String onOff = val ? VampireRevamp.getMessage(sender, GrammarMessageKeys.ON) : VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);
								String attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_NOSFERATU_ATTRIBUTE);
								VampireRevamp.sendMessage(sender,
										MessageType.INFO,
										CommandMessageKeys.SET_CHANGED_VALUE,
										"{player}", finalPlayer.getDisplayName(),
										"{attribute}", attributeName,
										"{value}", onOff);
							} else {
								VampireRevamp.sendMessage(sender,
										MessageType.ERROR,
										CommandMessageKeys.SET_ERROR_HYBRID,
										"{player}", finalPlayer.getDisplayName());
							}
						}, () -> {
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									CommandMessageKeys.DATA_NOT_FOUND);
						}, true);
						if (!success) {
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									CommandMessageKeys.DATA_NOT_FOUND);
						}
					}
					else {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								CommandMessageKeys.NO_PLAYER_FOUND,
								"{player}", targetName);
					}
				}
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NOT_VALID_VALUE,
						"{value}", yesno);
				sender.sendMessage(getCommandSyntax("set nosferatu"));
			}
		}

		@Subcommand("infection|i")
		@CommandPermission("vampire.set.infection")
		@CommandCompletion("@range:0-1 @players")
		@Description("{@@commands.set_infection_description}")
		@Syntax("<val> [player=you]")
		public void onSetInfection(CommandSender sender, double value, @Optional String targetName) {
			Double res = MathUtil.limitNumber(value, 0D, 100D);

			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);
			} else if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NOT_ENOUGH_INPUTS);
				sender.sendMessage(getCommandSyntax("set infection"));
				return;
			}

			if (player != null) {
				Player finalPlayer = player;
				boolean success = VampireRevamp.getPlayerCollection().getDataSynchronous(new Serializable[]{player.getUniqueId()}, (uplayer) -> {
					if (uplayer.isVampire()) {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								VampirismMessageKeys.ALREADY_VAMPIRE,
								"{player}", finalPlayer.getDisplayName());
					} else {
						if (!VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
							InfectionReason reason = uplayer.getReason();
							UPlayer maker = VampireRevamp.getPlayerCollection().getDataNow(new Serializable[]{uplayer.getMakerUUID()});
							if (reason == null) {
								reason = InfectionReason.OPERATOR;
								maker = null;
							}
							uplayer.setInfection(0);
							uplayer.addInfection(res, reason, maker);
							uplayer.update();

							String attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_INFECTION_ATTRIBUTE);
							VampireRevamp.sendMessage(sender,
									MessageType.INFO,
									CommandMessageKeys.SET_CHANGED_VALUE,
									"{player}", finalPlayer.getDisplayName(),
									"{attribute}", attributeName,
									"{value}", String.format("%.2f%%", value * 100));
						} else {
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									CommandMessageKeys.SET_ERROR_HYBRID,
									"{player}", finalPlayer.getDisplayName());
						}
					}
				}, () -> {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND);
				}, true);
				if (!success) {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND);
				}
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NO_PLAYER_FOUND,
						"{player}", targetName);
			}
		}

		@Subcommand("food|f")
		@CommandPermission("vampire.set.food")
		@CommandCompletion("@range:0-20 @players")
		@Description("{@@commands.set_food_description}")
		@Syntax("<val> [player=you]")
		public void onSetFood(CommandSender sender, int value, @Optional String targetName) {
			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);

				if (player == null) {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.NO_PLAYER_FOUND,
							"{player}", targetName);
					return;
				}
			}
			else if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NOT_ENOUGH_INPUTS);
				sender.sendMessage(getCommandSyntax("set food"));
				return;
			}

			Integer res = MathUtil.limitNumber(value, 0, 20);
			player.setFoodLevel(res);

			String attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_FOOD_ATTRIBUTE);
			VampireRevamp.sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					"{player}", player.getDisplayName(),
					"{attribute}", attributeName,
					"{value}", String.format("%d", res));
		}

		@Subcommand("health|h")
		@CommandPermission("vampire.set.health")
		@CommandCompletion("@range:0-20 @players")
		@Description("{@@commands.set_health_description}")
		@Syntax("<val> [player=you]")
		public void onSetHealth(CommandSender sender, int value, @Optional String targetName) {
			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);

				if (player == null) {
					VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.NO_PLAYER_FOUND,
							"{player}", targetName);
					return;
				}
			}
			else if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.NOT_ENOUGH_INPUTS);
				sender.sendMessage(getCommandSyntax("set health"));
				return;
			}

			Integer res = MathUtil.limitNumber(value, 0, (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setHealth(res);

			String attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_HEALTH_ATTRIBUTE);
			VampireRevamp.sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					"{player}", player.getDisplayName(),
					"{attribute}", attributeName,
					"{value}", String.format("%d", res));
		}
	}

	public String getCommandSyntax(String commandName) {
		String result = "";

		if (commandMap.containsKey(commandName)) {
			RegisteredCommand command = commandMap.get(commandName);
			result = "/" + command.getCommand() + " " +command.getSyntaxText();
		}

		return result;
	}
}
