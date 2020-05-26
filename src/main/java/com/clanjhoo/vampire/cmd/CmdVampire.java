package com.clanjhoo.vampire.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import co.aikar.commands.annotation.*;
import com.clanjhoo.vampire.BloodFlaskUtil;
import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.Perm;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.MConf;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.*;
import me.libraryaddict.disguise.DisguiseAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

@CommandAlias("v|vampire")
@CommandPermission("vampire.basecommand")
public class CmdVampire extends BaseCommand {

	private final VampireRevamp plugin;

	public CmdVampire(VampireRevamp plugin) {
		this.plugin = plugin;
	}

	@HelpCommand
	@Subcommand("help|h|?")
	@CommandCompletion("@range:1-2")
	@Syntax("[page=1]")
	public void onHelp(CommandSender sender, CommandHelp help) {
		if (help.getSearch() == null || help.getSearch().isEmpty() || !help.getSearch().get(0).equalsIgnoreCase("set")) {
			int maxPages = 2;
			Map<String, RegisteredCommand> commandMap = new HashMap<>();

			RootCommand vampireCommand = plugin.pumanager.getRegisteredRootCommands().iterator().next();

			sender.spigot().sendMessage(TextUtil.getHelpHeader(help, maxPages, help.getCommandName()));
			for (Entry<String, RegisteredCommand> entry : vampireCommand.getSubCommands().entries()) {
				commandMap.put(entry.getKey(), entry.getValue());
				//Bukkit.getLogger().log(Level.INFO, "Key: " + entry.getKey());
			}

			if (help.getPage() == 1) {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("help", commandMap.get("help"), sender, CollectionUtil.list("?", "h", "help"), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("show", commandMap.get("show"), sender, plugin.mConf.getAliasesVampireShow(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("bloodlust", commandMap.get("bloodlust"), sender, plugin.mConf.getAliasesVampireModeBloodlust(), 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("intend", commandMap.get("intend"), sender, plugin.mConf.getAliasesVampireModeIntend(), 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("nightvision", commandMap.get("nightvision"), sender, plugin.mConf.getAliasesVampireModeNightvision(), 1));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("offer", commandMap.get("offer"), sender, plugin.mConf.getAliasesVampireOffer(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("accept", commandMap.get("accept"), sender, plugin.mConf.getAliasesVampireAccept(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("flask", commandMap.get("flask"), sender, plugin.mConf.getAliasesVampireFlask(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("shriek", commandMap.get("shriek"), sender, plugin.mConf.getAliasesVampireShriek(), 0));
			} else {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("batusi", commandMap.get("batusi"), sender, plugin.mConf.getAliasesVampireModeBatusi(), 2));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("list", commandMap.get("list"), sender, plugin.mConf.getAliasesVampireList(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("set", commandMap.get("set"), sender, plugin.mConf.getAliasesVampireSet(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("version", commandMap.get("version"), sender, plugin.mConf.getAliasesVampireVersion(), 0));
			}
		}
		else {
			(new CmdVampireSet()).onHelp(sender, help);
		}
	}

	@Subcommand("show")
	@CommandCompletion("@players")
	@CommandPermission("vampire.show")
	@Description("show player info")
	@Syntax("[player=you]")
	public void onShow(CommandSender sender, @Optional String targetName) {
		if (sender instanceof Player || targetName != null) {
			Player player;
			UPlayer uplayer;
			if (targetName == null) {
				player = (Player) sender;
			}
			else {
				player = Bukkit.getPlayer(targetName);
			}
			uplayer = UPlayer.get(player);

			MConf mconf = plugin.mConf;
			if (player != null && uplayer != null) {

				boolean self = sender instanceof Player && ((Player) sender).getUniqueId().equals(player.getUniqueId());

				// Test permissions
				if (self || Perm.SHOW_OTHER.has(sender, true)) {

					String You = "You";
					String are = "are";
					if (!self) {
						You = player.getDisplayName();
						are = "is";
					}

					sender.spigot().sendMessage(TextUtil.getPlayerInfoHeader(uplayer.isVampire(),
																			 uplayer.isNosferatu(),
																			 player.getDisplayName()));
					if (uplayer.isVampire()) {
						String vampireType = ChatColor.RED + "vampire";
						if (uplayer.isNosferatu())
							vampireType = ChatColor.DARK_RED + "nosferatu";
						sender.sendMessage(ChatColor.YELLOW + "" +
											ChatColor.ITALIC + You +
											ChatColor.RESET + " " +
											ChatColor.YELLOW + are + " a " + vampireType +
											ChatColor.YELLOW + ".");
						msg(sender, uplayer.getReasonDesc(self));

						msg(sender, uplayer.bloodlustMsg());
						msg(sender, uplayer.intendMsg());
						msg(sender, uplayer.usingNightVisionMsg());

						msg(sender, "<k>Temperature <v>%d%%", (int) Math.round(uplayer.getTemp() * 100));

						int rad = percent(uplayer.getRad());
						int sun = percent(SunUtil.calcSolarRad(player.getWorld(), player));
						double terrain = 1d - SunUtil.calcTerrainOpacity(player.getLocation().getBlock());
						double armor = 1d - SunUtil.calcArmorOpacity(player);
						int base = percent(mconf.getBaseRad());
						msg(sender, "<k>Irradiation <v>X% <k>= <yellow>sun <lime>*terrain <blue>*armor <silver>-base");
						msg(sender, "<k>Irradiation <v>%+d%% <k>= <yellow>%d <lime>*%.2f <blue>*%.2f <silver>%+d", rad, sun, terrain, armor, base);
					}
					else if (uplayer.isInfected()) {
						msg(sender, "<i>" + You + " <i>" + are + " infected at <h>%d%%<i>.", percent(uplayer.getInfection()));
						msg(sender, uplayer.getReasonDesc(self));
					} else {
						msg(sender, "<i>" + You + " <i>" + are + " neither vampire nor infected with the dark disease.");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
			sender.sendMessage(ChatColor.AQUA + "/v,vampire show <player>");
		}
	}

	private static void msg(CommandSender receiver, String message) {
		receiver.sendMessage(TextUtil.parse(message));
	}

	private static void msg(CommandSender receiver, String message, Object... objects) {
		receiver.sendMessage(TextUtil.parse(message, objects));
	}

	private static int percent(double quota) {
		return (int)Math.round(quota*100);
	}

	@Subcommand("bloodlust|modebloodlust|b")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.bloodlust")
	@Description("use bloodlust")
	@Syntax("[yes/no=toggle]")
	public void onModeBloodlust(Player sender, @Optional String yesno) {
		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			UPlayer uplayer = UPlayer.get(sender);

			if (uplayer != null) {
				if (uplayer.isVampire()) {
					boolean isActive = uplayer.isBloodlusting();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setBloodlusting(!isActive);
				}
				else {
					sender.sendMessage("Only vampires can use bloodlust.");
				}
			} else {
				sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
		}
	}

	@Subcommand("intend|modeintend|i")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.intent")
	@Description("use intent to infect")
	@Syntax("[yes/no=toggle]")
	public void onModeIntend(Player sender, @Optional String yesno) {
		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			UPlayer uplayer = UPlayer.get(sender);

			if (uplayer != null) {
				if (uplayer.isVampire()) {
					boolean isActive = uplayer.isIntending();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setIntending(!isActive);
				}
				else {
					sender.sendMessage("Only vampires can infect on purpose.");
				}
			} else {
				sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
		}
	}

	@Subcommand("nightvision|modenightvision|nv")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.nightvision")
	@Description("use nightvision")
	@Syntax("[yes/no=toggle]")
	public void onModeNightvision(Player sender, @Optional String yesno) {
		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			UPlayer uplayer = UPlayer.get(sender);

			if (uplayer != null) {
				if (uplayer.isVampire()) {
					boolean isActive = uplayer.isUsingNightVision();

					if (yesno != null) {
						isActive = yesno.equals("no");
					}

					uplayer.setUsingNightVision(!isActive);
				}
				else {
					sender.sendMessage("Only vampires can use nightvision.");
				}
			} else {
				sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
		}
	}

	@Subcommand("offer|o")
	@CommandCompletion("@players @range:1-20")
	@CommandPermission("vampire.trade.offer")
	@Description("offer blood to someone")
	@Syntax("<player> [amount=4.0]")
	public void onOffer(Player sender, String targetName, @Default("4") double rawamount) {
		UPlayer vme = UPlayer.get(sender);
		UPlayer vyou = UPlayer.get(Bukkit.getPlayer(targetName));

		if (vme != null && vyou != null) {
			double unlimitedAmount = rawamount;

			double amount = MathUtil.limitNumber(unlimitedAmount, 0D, 20D);
			if (amount != unlimitedAmount) {
				msg(sender, "<b>amount must be between 0.0 and 20.0");
				return;
			}

			vme.tradeOffer(vyou, amount);
		}
		else {
			sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
		}
	}

	@Subcommand("accept|a")
	@CommandPermission("vampire.trade.accept")
	@Description("accept blood offer")
	public void onAccept(Player sender) {
		UPlayer vme = UPlayer.get(sender);

		if (vme != null) {
			vme.tradeAccept();
		}
		else {
			sender.sendMessage("Couldn't find  your vampire data. Please contact an administrator.");
		}
	}

	@Subcommand("flask|f")
	@CommandCompletion("@range:1-20")
	@CommandPermission("vampire.flask")
	@Description("create blood flask")
	@Syntax("[amount=4.0]")
	public void onFlask(Player sender, @Default("4") double amount) {
		UPlayer vme = UPlayer.get(sender);

		if (vme != null) {
			// Does the player have the required amount?
			if ((vme.isVampire() && amount > vme.getFood().get()) || ( ! vme.isVampire() && amount > sender.getHealth())) {
				vme.msg(plugin.mLang.flaskInsufficient);
			}
			else {
				// ... create a blood flask!
				if (vme.isVampire()) {
					vme.getFood().add(-amount);
				}
				else {
					sender.setHealth(sender.getHealth() - amount);
				}
				BloodFlaskUtil.fillBottle(amount, vme);

				// Inform
				vme.msg(plugin.mLang.flaskSuccess);
			}
		}
		else {
			sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
		}
	}

	@Subcommand("shriek")
	@CommandPermission("vampire.shriek")
	@Description("shriek")
	public void onShriek(Player sender) {
		UPlayer vme = UPlayer.get(sender);

		if (vme != null) {
			if (vme.isVampire())
				vme.shriek();
			else
				sender.sendMessage("Only vampires can shriek.");
		}
		else {
			sender.sendMessage("Couldn't find  your vampire data. Please contact an administrator.");
		}
	}

	@Subcommand("list")
	@CommandCompletion("@range:1-5")
	@CommandPermission("vampire.list")
	@Description("list vampires and infected")
	@Syntax("[page=1]")
	public void onList(CommandSender sender, @Default("1") int page) {
		List<String> vampiresOnline = new ArrayList<>();
		List<String> vampiresOffline = new ArrayList<>();
		List<String> infectedOnline = new ArrayList<>();
		List<String> infectedOffline = new ArrayList<>();

		for (UPlayer uplayer : plugin.uPlayerColl.getAll()) {
			if (uplayer.getOfflinePlayer() != null) {
				OfflinePlayer p = uplayer.getOfflinePlayer();
				if (uplayer.isVampire()) {
					if (p.isOnline()) {
						vampiresOnline.add(ChatColor.WHITE.toString() + p.getName());
					} else {
						vampiresOffline.add(ChatColor.WHITE.toString() +  p.getName());
					}
				} else if (uplayer.isInfected()) {
					if (p.isOnline()) {
						infectedOnline.add(ChatColor.WHITE.toString() +  p.getName());
					} else {
						infectedOffline.add(ChatColor.WHITE.toString() +  p.getName());
					}
				}
			}
		}

		// Create Messages
		List<String> lines = new ArrayList<>();

		if (vampiresOnline.size() > 0)
		{
			lines.add("<h>=== Vampires Online ===");
			lines.add(TextUtil.implodeCommaAndDot(vampiresOnline, "<i>"));
		}

		if (vampiresOffline.size() > 0)
		{
			lines.add("<h>=== Vampires Offline ===");
			lines.add(TextUtil.implodeCommaAndDot(vampiresOffline, "<i>"));
		}

		if (infectedOnline.size() > 0)
		{
			lines.add("<h>=== Infected Online ===");
			lines.add(TextUtil.implodeCommaAndDot(infectedOnline, "<i>"));
		}

		if (infectedOffline.size() > 0)
		{
			lines.add("<h>=== Infected Offline ===");
			lines.add(TextUtil.implodeCommaAndDot(infectedOffline, "<i>"));
		}

		// Send them
		lines = TextUtil.parseWrap(lines);
		for (String line : lines) {
			sender.sendMessage(line);
		}
	}

	@Subcommand("version|v")
	@CommandPermission("vampire.version")
	@Description("display plugin version")
	public void onVersion(CommandSender sender) {
		List<BaseComponent[]> pd = TextUtil.getPluginDescription(plugin);
		for (BaseComponent[] mess : pd) {
			sender.spigot().sendMessage(mess);
		}
	}

	@Subcommand("batusi")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.batusi")
	@Description("use batcloud")
	@Syntax("[yes/no=toggle]")
	public void onModeBatusi(Player sender, @Optional String yesno) {
		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			UPlayer uplayer = UPlayer.get(sender);

			if (uplayer != null) {
				if (uplayer.isNosferatu()) {
					boolean activate = !plugin.batEnabled.getOrDefault(sender.getUniqueId(), false);

					if (yesno != null) {
						activate = yesno.equals("yes");
					}

					if (activate) {
						if (!plugin.batEnabled.getOrDefault(sender.getUniqueId(), false)) {
							EntityUtil.spawnBats(sender, 9);
							if (plugin.isDisguiseEnabled)
								DisguiseUtil.disguiseBat(sender);
							plugin.batEnabled.put(sender.getUniqueId(), true);
							sender.setAllowFlight(true);
							sender.setFlying(true);
							sender.sendMessage("You're now a cloud of bats");
						} else {
							if (plugin.isDisguiseEnabled)
								DisguiseUtil.disguiseBat(sender);
							sender.setAllowFlight(true);
							sender.setFlying(true);
							sender.sendMessage("You already are a cloud of bats!");
						}
					}
					else {
						try {
							EntityUtil.despawnBats(sender);
							if (plugin.isDisguiseEnabled)
								DisguiseAPI.undisguiseToAll(sender);
							sender.setAllowFlight(false);
							sender.setFlying(false);
							plugin.batEnabled.put(sender.getUniqueId(), false);
							sender.sendMessage("You're no longer a cloud of bats!");
						}
						catch (Exception ex) {
							sender.sendMessage("Error while removing bat cloud! See the console for the full stacktrace.");
							plugin.getLogger().log(Level.WARNING, "Error while removing bat cloud!: " + ex.getMessage());
							ex.printStackTrace();
						}
					}
				}
				else {
					sender.sendMessage("Only nosferatu can turn into a batcloud.");
				}
			} else {
				sender.sendMessage("Couldn't find vampire data for that player. Please contact an administrator.");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
		}
	}

	@Subcommand("set")
	@CommandPermission("vampire.set")
	@Description("set player attributes")
	public class CmdVampireSet extends BaseCommand {
		@Subcommand("help|h|?")
		public void onHelp(CommandSender sender, CommandHelp help) {
			int maxPages = 1;
			Map<String, RegisteredCommand> commandMap = new HashMap<>();
			RootCommand vampireCommand = plugin.pumanager.getRegisteredRootCommands().iterator().next();

			sender.spigot().sendMessage(TextUtil.getHelpHeader(help, maxPages, "set"));
			for (Entry<String, RegisteredCommand> entry : vampireCommand.getSubCommands().entries()) {
				commandMap.put(entry.getKey(), entry.getValue());
				//Bukkit.getLogger().log(Level.INFO, "Key: " + entry.getKey());
			}

			if (help.getPage() == 1) {
				sender.spigot().sendMessage(TextUtil.getCommandHelp("vampire", commandMap.get("set vampire"), sender, plugin.mConf.getAliasesVampireSetVampire(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("nosferatu", commandMap.get("set nosferatu"), sender, plugin.mConf.getAliasesVampireSetNosferatu(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("infection", commandMap.get("set infection"), sender, plugin.mConf.getAliasesVampireSetInfection(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("food", commandMap.get("set food"), sender, plugin.mConf.getAliasesVampireSetFood(), 0));
				sender.spigot().sendMessage(TextUtil.getCommandHelp("health", commandMap.get("set health"), sender, plugin.mConf.getAliasesVampireSetHealth(), 0));
			}
		}

		@Subcommand("vampire|v")
		@CommandCompletion("@yesno @players")
		@Description("set vampire (yes or no)")
		@Syntax("<yes/no> [player=you]")
		public void onSetVampire(CommandSender sender, String yesno, @Optional String targetName) {
			if (yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
				boolean val = yesno.equalsIgnoreCase("yes");
				Perm perm = val ? Perm.SET_VAMPIRE_TRUE : Perm.SET_VAMPIRE_FALSE;
				if (perm.has(sender, true)) {
					UPlayer uplayer = null;
					Player player = null;
					if (targetName != null) {
						player = Bukkit.getPlayer(targetName);
					} else if (sender instanceof Player) {
						player = (Player) sender;
					} else {
						sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
						sender.sendMessage(ChatColor.AQUA + "/v,vampire set vampire <val> <player>");
					}

					uplayer = UPlayer.get(player);
					if (uplayer != null) {
						if (uplayer.isVampire() != val) {
							uplayer.setReason(InfectionReason.OPERATOR);
							uplayer.setMaker(null);
							uplayer.setVampire(val);

							uplayer.update();
						}
						sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " now has vampire = " + val);
					}
					else if (targetName != null) {
						sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
			}
		}

		@Subcommand("nosferatu|n")
		@CommandCompletion("@yesno @players")
		@Description("set nosferatu (yes or no)")
		@Syntax("<yes/no> [player=you]")
		public void onSetNosferatu(CommandSender sender, String yesno, @Optional String targetName) {
			if (yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
				boolean val = yesno.equalsIgnoreCase("yes");
				Perm perm = val ? Perm.SET_NOSFERATU_TRUE : Perm.SET_NOSFERATU_FALSE;
				if (perm.has(sender, true)) {
					UPlayer uplayer = null;
					Player player = null;
					if (targetName != null) {
						player = Bukkit.getPlayer(targetName);
					} else if (sender instanceof Player) {
						player = (Player) sender;
					} else {
						sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
						sender.sendMessage(ChatColor.AQUA + "/v,vampire set nosferatu <val> <player>");
					}

					uplayer = UPlayer.get(player);
					if (uplayer != null) {
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

						sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " now has nosferatu = " + val);
					}
					else if (targetName != null) {
						sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "\"" + ChatColor.LIGHT_PURPLE + yesno + ChatColor.RED + "\" is not a valid yes/no value.");
			}
		}

		@Subcommand("infection|i")
		@CommandPermission("vampire.set.infection")
		@CommandCompletion("@range:0-1 @players")
		@Description("set infection (0 to 1)")
		@Syntax("<val> [player=you]")
		public void onSetInfection(CommandSender sender, double value, @Optional String targetName) {
			Double res = MathUtil.limitNumber(value, 0D, 100D);

			UPlayer uplayer = null;
			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);
			} else if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
				sender.sendMessage(ChatColor.AQUA + "/v,vampire set infection <val> <player>");
			}

			uplayer = UPlayer.get(player);
			if (uplayer != null) {
				if (uplayer.isVampire()) {
					msg(sender, plugin.mLang.xIsAlreadyVamp, uplayer.getPlayer().getDisplayName());
				}
				else {
					uplayer.setReason(InfectionReason.OPERATOR);
					uplayer.setMaker(null);
					uplayer.setInfection(res);
					uplayer.addInfection(0, InfectionReason.OPERATOR, null);

					uplayer.update();

					sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " now has infection = " + value);
				}
			}
			else if (targetName != null) {
				sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
			}
		}

		@Subcommand("food|f")
		@CommandPermission("vampire.set.food")
		@CommandCompletion("@range:0-20 @players")
		@Description("set food (0 to 20)")
		@Syntax("<val> [player=you]")
		public void onSetFood(CommandSender sender, int value, @Optional String targetName) {
			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);
			}
			else if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
				sender.sendMessage(ChatColor.AQUA + "/v,vampire set food <val> <player>");
			}

			if (player != null) {
				Integer res = MathUtil.limitNumber(value, 0, 20);
				player.setFoodLevel(res);
				sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " now has food = " + value);
			}
			else if (targetName != null) {
				sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
			}
		}

		@Subcommand("health|h")
		@CommandPermission("vampire.set.health")
		@CommandCompletion("@range:0-20 @players")
		@Description("set health (0 to 20)")
		@Syntax("<val> [player=you]")
		public void onSetHealth(CommandSender sender, int value, @Optional String targetName) {
			Player player = null;
			if (targetName != null) {
				player = Bukkit.getPlayer(targetName);
			}
			else if (sender instanceof Player) {
				player = (Player) sender;
			}
			else {
				sender.sendMessage(ChatColor.RED + "Not enough command input. " + ChatColor.YELLOW + "You should use it like this:");
				sender.sendMessage(ChatColor.AQUA + "/v,vampire set health <val> <player>");
			}

			if (player != null) {
				Integer res = MathUtil.limitNumber(value, 0, (int) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				player.setHealth(res);
				sender.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " now has health = " + value);
			}
			else if (targetName != null) {
				sender.sendMessage(ChatColor.RED + "No player matches: \"" + ChatColor.LIGHT_PURPLE + targetName + ChatColor.RED + "\"");
			}
		}
	}
}
