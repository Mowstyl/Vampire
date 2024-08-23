package com.clanjhoo.vampire.cmd;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import com.clanjhoo.vampire.*;
import com.clanjhoo.vampire.keyproviders.CommandMessageKeys;
import com.clanjhoo.vampire.keyproviders.GrammarMessageKeys;
import com.clanjhoo.vampire.keyproviders.SkillMessageKeys;
import com.clanjhoo.vampire.keyproviders.VampirismMessageKeys;
import com.clanjhoo.vampire.config.PluginConfig;
import com.clanjhoo.vampire.entity.VPlayer;
import com.clanjhoo.vampire.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

@CommandAlias("v|vampire")
@CommandPermission("vampire.basecommand")
public class CmdVampire extends BaseCommand {
	private final Map<String, RegisteredCommand> commandMap;

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

			VampireRevamp.sendMessage(sender, TextUtil.getHelpHeader(help, maxPages, help.getCommandName(), sender));

			if (help.getPage() == 1) {
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("help", commandMap.get("help"), sender,  0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("show", commandMap.get("show"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("bloodlust", commandMap.get("bloodlust"), sender, 1));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("intend", commandMap.get("intend"), sender, 1));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("nightvision", commandMap.get("nightvision"), sender, 1));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("offer", commandMap.get("offer"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("accept", commandMap.get("accept"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("flask", commandMap.get("flask"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("shriek", commandMap.get("shriek"), sender, 0));
			} else {
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("batusi", commandMap.get("batusi"), sender, 2));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("list", commandMap.get("list"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("set", commandMap.get("set"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("version", commandMap.get("version"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("reload", commandMap.get("reload"), sender, 0));
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
			OfflinePlayer rawPlayer;
			if (targetName == null) {
				rawPlayer = (Player) sender;
			}
			else {
				rawPlayer = Bukkit.getPlayer(targetName);
				if (rawPlayer == null) {
					rawPlayer = Bukkit.getOfflinePlayer(targetName);
					if (!rawPlayer.hasPlayedBefore()) {
						rawPlayer = null;
					}
				}
			}
			OfflinePlayer player = rawPlayer;
			if (player != null) {
				PluginConfig conf = VampireRevamp.getVampireConfig();
				boolean self = sender instanceof Player && ((Player) sender).getUniqueId().equals(player.getUniqueId());

				// Test permissions
				if (self || Perm.SHOW_OTHER.has(sender, true)) {
					VampireRevamp.syncTaskVPlayer(
                            player,
							(vPlayer) -> {
								Component[] youAreWere = VampireRevamp.getYouAreWere(sender, player, self);
								Component you = youAreWere[0];
								Component are = youAreWere[1];
								Component were = youAreWere[2];
								Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
								Component on = VampireRevamp.getMessage(sender, GrammarMessageKeys.ON);
								Component off = VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);

								Component playerName = Component.text(player.getName());

								VampireRevamp.sendMessage(sender, TextUtil.getPlayerInfoHeader(vPlayer.isVampire(),
										vPlayer.isNosferatu(),
										playerName,
										sender));
								if (vPlayer.isVampire()) {
									if (vPlayer.isNosferatu())
										vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
									VampireRevamp.sendMessage(sender,
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
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											reason.getDescKey(),
											new Tuple<>("{player}", you),
											new Tuple<>("{to_be_past}", were),
											new Tuple<>("{parent}", Component.text(parent)));

									Component bloodlustName = VampireRevamp.getMessage(sender, GrammarMessageKeys.BLOODLUST);
									bloodlustName = TextUtil.capitalizeFirst(bloodlustName);
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SHOW_BLOODLUST,
											new Tuple<>("{bloodlust}", bloodlustName),
											new Tuple<>("{enabled}", vPlayer.isBloodlusting() ? on : off),
											new Tuple<>("{percent}", Component.text(String.format("%.1f%%", vPlayer.combatDamageFactor() * 100))));

									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SHOW_INTENT,
											new Tuple<>("{enabled}", vPlayer.isIntending() ? on : off),
											new Tuple<>("{percent}", Component.text(String.format("%.1f%%", vPlayer.combatInfectRisk() * 100))));

									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SHOW_NIGHTVISION,
											new Tuple<>("{enabled}", vPlayer.isUsingNightVision() ? on : off));

									if (player instanceof Player) {
										VampireRevamp.sendMessage(sender,
												MessageType.INFO,
												CommandMessageKeys.SHOW_TEMPERATURE,
												"{percent}", String.format("%d%%", (int) Math.round(vPlayer.getTemp() * 100)));

										int rad = (int) Math.round(100 * vPlayer.getRad());
										int sun = (int) Math.round(100 * SunUtil.calcSolarRad(((Player) player).getWorld(), ((Player) player)));
										double terrain = 1d - SunUtil.calcTerrainOpacity(((Player) player).getLocation().getBlock());
										double armor = 1d - SunUtil.calcArmorOpacity((Player) player);
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
								} else if (vPlayer.isInfected()) {
									VampireRevamp.sendMessage(sender,
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
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											reason.getDescKey(),
											new Tuple<>("{player}", you),
											new Tuple<>("{to_be_past}", were),
											new Tuple<>("{parent}", Component.text(parent)));
								} else {
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SHOW_CURED,
											new Tuple<>("{player}", you),
											new Tuple<>("{to_be}", are),
											new Tuple<>("{vampire_type}", vampireType));
								}
							},
							(ex) -> VampireRevamp.sendMessage(sender,
                                    MessageType.ERROR,
                                    CommandMessageKeys.DATA_NOT_FOUND));
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
			VampireRevamp.syncTaskVPlayer(
					sender,
					(vPlayer) -> {
						VampireRevamp.debugLog(Level.INFO, "Acceptance");
						if (vPlayer.isVampire()) {
							VampireRevamp.debugLog(Level.INFO, "Le vampire");
							boolean isActive = vPlayer.isBloodlusting();

							if (yesno != null) {
								isActive = yesno.equals("no");
							}

							vPlayer.setBloodlusting(!isActive);
						} else {
							VampireRevamp.debugLog(Level.INFO, "Non non non!");
							Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
							Component bloodlustAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.BLOODLUST);
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
									new Tuple<>("{vampire_type}", vampireType),
									new Tuple<>("{action}", bloodlustAction));
						}
					},
					(ex) -> VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND));
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
			VampireRevamp.syncTaskVPlayer(
					sender,
					(vPlayer) -> {
						if (vPlayer.isVampire()) {
							boolean isActive = vPlayer.isIntending();

							if (yesno != null) {
								isActive = yesno.equals("no");
							}

							vPlayer.setIntending(!isActive);
						} else {
							Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
							Component intentAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.INTEND);
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
									new Tuple<>("{vampire_type}", vampireType),
									new Tuple<>("{action}", intentAction));
						}
					},
					(ex) -> VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND));
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
			VampireRevamp.syncTaskVPlayer(
					sender,
					(vPlayer) -> {
						if (vPlayer.isVampire()) {
							boolean isActive = vPlayer.isUsingNightVision();

							if (yesno != null) {
								isActive = yesno.equals("no");
							}

							vPlayer.setUsingNightVision(!isActive);
						} else {
							Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
							Component nvAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.NIGHTVISION);
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
									new Tuple<>("{vampire_type}", vampireType),
									new Tuple<>("{action}", nvAction));
						}
					},
					(ex) -> VampireRevamp.sendMessage(sender,
                            MessageType.ERROR,
                            CommandMessageKeys.DATA_NOT_FOUND));
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

			VampireRevamp.syncTaskVPlayer(
					sender,
					(vme) -> VampireRevamp.syncTaskVPlayer(
							pyou,
                            (vyou) -> vme.tradeOffer(sender, vyou, amount),
                            (ex) -> VampireRevamp.sendMessage(sender,
                                    MessageType.ERROR,
                                    CommandMessageKeys.DATA_NOT_FOUND)),
					(ex) -> VampireRevamp.sendMessage(sender,
							MessageType.ERROR,
							CommandMessageKeys.DATA_NOT_FOUND));
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
		VampireRevamp.syncTaskVPlayer(
				sender,
				VPlayer::tradeAccept,
				(ex) -> VampireRevamp.sendMessage(sender,
						MessageType.ERROR,
						CommandMessageKeys.DATA_NOT_FOUND));
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

		VampireRevamp.syncTaskVPlayer(
				sender,
				(vme) -> {
					// Does the player have the required amount?
					boolean consumeFood = VampireRevamp.getVampireConfig().general.vampiresUseFoodAsBlood && vme.isVampire();
					if ((consumeFood && amount > vme.getFood()) || (!consumeFood && amount > sender.getHealth())) {
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								SkillMessageKeys.FLASK_INSUFFICIENT);
					} else {
						// ... create a blood flask!
						if (BloodFlaskUtil.fillBottle(vme, amount)) {
							if (consumeFood) {
								vme.addFood(-amount);
							} else {
								sender.setHealth(sender.getHealth() - amount);
							}
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
				},
				(ex) -> VampireRevamp.sendMessage(sender,
                        MessageType.ERROR,
                        CommandMessageKeys.DATA_NOT_FOUND));
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

		VampireRevamp.syncTaskVPlayer(
				sender,
				(vme) -> {
					if (vme.isVampire()) {
						vme.shriek();
					} else {
						Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
						Component shriekAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.SHRIEK);
						VampireRevamp.sendMessage(sender,
								MessageType.ERROR,
								GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
								new Tuple<>("{vampire_type}", vampireType),
								new Tuple<>("{action}", shriekAction));
					}
				},
				(ex) -> VampireRevamp.sendMessage(sender,
                        MessageType.ERROR,
                        CommandMessageKeys.DATA_NOT_FOUND));
	}

	@Subcommand("list")
	@CommandCompletion("@range:1-5")
	@CommandPermission("vampire.list")
	@Description("{@@commands.list_description}")
	@Syntax("[page=1]")
	public void onList(CommandSender sender, @Default("1") int page) {
		List<Component> vampiresOnline = new ArrayList<>();
		List<Component> infectedOnline = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			VPlayer vPlayer = VampireRevamp.getVPlayerNow(player);
			if (vPlayer == null)
				continue;
			Player auxPlayer = vPlayer.getPlayer();
			if (auxPlayer == null)
				continue;

			Component compName = Component.text(auxPlayer.getName());
			if (vPlayer.isVampire())
				vampiresOnline.add(compName);
			else if (vPlayer.isInfected())
				infectedOnline.add(compName);
		}

		// Create Messages
		List<Component> lines = new ArrayList<>();

		Component vampireType = VampireRevamp.getMessage(sender, GrammarMessageKeys.VAMPIRE_TYPE);
		vampireType = TextUtil.capitalizeFirst(vampireType);
		Component infectedType = VampireRevamp.getMessage(sender, GrammarMessageKeys.INFECTED_TYPE);
		infectedType = TextUtil.capitalizeFirst(infectedType);
		Component onlineStr = VampireRevamp.getMessage(sender, GrammarMessageKeys.ONLINE);
		//String offlineStr = VampireRevamp.getMessage(sender, GrammarMessageKeys.OFFLINE);

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
		// lines = TextUtil.wrap(lines);
		for (Component line : lines) {
			VampireRevamp.sendMessage(sender, line);
		}
	}

	@Subcommand("version|v")
	@CommandPermission("vampire.version")
	@Description("{@@commands.version_description}")
	public void onVersion(CommandSender sender) {
		List<Component> pd = TextUtil.getPluginDescription(VampireRevamp.getInstance());
		for (Component mess : pd) {
			VampireRevamp.sendMessage(sender, mess);
		}
	}

	@Subcommand("batusi|bat")
	@CommandCompletion("@yesno")
	@CommandPermission("vampire.mode.batusi")
	@Description("{@@commands.batusi_description}")
	@Syntax("[yes/no=toggle] [numberOfBats=config_default]")
	public void onModeBatusi(Player sender, @Optional String yesno, @Optional Integer numberOfBats) {
		if (VampireRevamp.getVampireConfig().general.isBlacklisted(sender.getWorld())) {
			VampireRevamp.sendMessage(sender,
					MessageType.ERROR,
					CommandMessageKeys.BLACKLISTED_WORLD);
			return;
		}

		if (yesno == null || yesno.equalsIgnoreCase("yes") || yesno.equalsIgnoreCase("no")) {
			VampireRevamp plugin = VampireRevamp.getInstance();
			VampireRevamp.syncTaskVPlayer(
					sender,
					(vPlayer) -> {
						if (!VampireRevamp.getVampireConfig().vampire.batusi.nosferatuOnly || vPlayer.isNosferatu()) {
							boolean activate = !plugin.batEnabled.getOrDefault(sender.getUniqueId(), false);
							int numBats = 0;

							if (yesno != null) {
								activate = yesno.equals("yes");
							}

							if (activate) {
								int defNumBats = VampireRevamp.getVampireConfig().vampire.batusi.numberOfBats;
								int maxNumBats = VampireRevamp.getVampireConfig().vampire.batusi.maxBats;
								if (defNumBats < 0) {
									VampireRevamp.sendMessage(sender,
											MessageType.ERROR,
											CommandMessageKeys.BATUSI_DEFVALUE_ERROR);
									defNumBats = 0;
								}
								if (maxNumBats < 0) {
									VampireRevamp.sendMessage(sender,
											MessageType.ERROR,
											CommandMessageKeys.BATUSI_DEFVALUE_ERROR);
									maxNumBats = defNumBats;
								}
								if (numberOfBats == null) {
									numBats = defNumBats;
								}
								else {
									numBats = numberOfBats;
									if (numBats < 0) {
										VampireRevamp.sendMessage(sender,
												MessageType.ERROR,
												CommandMessageKeys.BATUSI_NEGATIVE_BATS);
										return;
									}
									if (numBats > maxNumBats) {
										VampireRevamp.sendMessage(sender,
												MessageType.ERROR,
												CommandMessageKeys.BATUSI_TOO_MANY,
												"{default_bats}", Integer.toString(maxNumBats));
										numBats = maxNumBats;
									}
								}
							}
							else if (numberOfBats != null) {
								VampireRevamp.sendMessage(sender,
										MessageType.INFO,
										CommandMessageKeys.BATUSI_IGNORED_BATS);
							}

							vPlayer.setBatusi(activate, numBats);
						} else {
							Component nosferatuType = VampireRevamp.getMessage(sender, GrammarMessageKeys.NOSFERATU_TYPE);
							Component batusiAction = VampireRevamp.getMessage(sender, GrammarMessageKeys.BATUSI);
							VampireRevamp.sendMessage(sender,
									MessageType.ERROR,
									GrammarMessageKeys.ONLY_TYPE_CAN_ACTION,
									new Tuple<>("{vampire_type}", nosferatuType),
									new Tuple<>("{action}", batusiAction));
						}
					},
					(ex) -> VampireRevamp.sendMessage(sender,
                            MessageType.ERROR,
                            CommandMessageKeys.DATA_NOT_FOUND));
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

			VampireRevamp.sendMessage(sender, TextUtil.getHelpHeader(help, maxPages, "set", sender));

			if (help.getPage() == 1) {
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("vampire", commandMap.get("set vampire"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("nosferatu", commandMap.get("set nosferatu"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("infection", commandMap.get("set infection"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("food", commandMap.get("set food"), sender, 0));
				VampireRevamp.sendMessage(sender, TextUtil.getCommandHelp("health", commandMap.get("set health"), sender, 0));
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
						VampireRevamp.syncTaskVPlayer(
								player,
								(vPlayer) -> {
									if (vPlayer.isVampire() != val) {
										if (!val || !VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
											vPlayer.setReason(InfectionReason.OPERATOR);
											vPlayer.setMaker(null);
											vPlayer.setVampire(val);

											vPlayer.update();

											Component onOff = val ? VampireRevamp.getMessage(sender, GrammarMessageKeys.ON) : VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);
											Component attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_VAMPIRE_ATTRIBUTE);
											Component displayName = Component.text(finalPlayer.getDisplayName());
											VampireRevamp.sendMessage(sender,
													MessageType.INFO,
													CommandMessageKeys.SET_CHANGED_VALUE,
													new Tuple<>("{player}", displayName),
													new Tuple<>("{attribute}", attributeName),
													new Tuple<>("{value}", onOff));
										} else {
											VampireRevamp.sendMessage(sender,
													MessageType.ERROR,
													CommandMessageKeys.SET_ERROR_HYBRID,
													"{player}", finalPlayer.getDisplayName());
										}
									}
								},
								(ex) -> VampireRevamp.sendMessage(sender,
                                        MessageType.ERROR,
                                        CommandMessageKeys.DATA_NOT_FOUND));
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
						VampireRevamp.syncTaskVPlayer(
								player,
								(vPlayer) -> {
									if (!val || !VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
										if (val && vPlayer.isVampire() != val) {
											vPlayer.setReason(InfectionReason.OPERATOR);
											vPlayer.setMaker(null);
											vPlayer.setVampire(val);

											vPlayer.update();
										}
										if (vPlayer.isNosferatu() != val) {
											vPlayer.setNosferatu(val);

											vPlayer.update();
										}

										Component onOff = val ? VampireRevamp.getMessage(sender, GrammarMessageKeys.ON) : VampireRevamp.getMessage(sender, GrammarMessageKeys.OFF);
										Component attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_NOSFERATU_ATTRIBUTE);
										Component displayName = Component.text(finalPlayer.getDisplayName());
										VampireRevamp.sendMessage(sender,
												MessageType.INFO,
												CommandMessageKeys.SET_CHANGED_VALUE,
												new Tuple<>("{player}", displayName),
												new Tuple<>("{attribute}", attributeName),
												new Tuple<>("{value}", onOff));
									}
									else {
										VampireRevamp.sendMessage(sender,
												MessageType.ERROR,
												CommandMessageKeys.SET_ERROR_HYBRID,
												"{player}", finalPlayer.getDisplayName());
									}
								},
								(ex) -> VampireRevamp.sendMessage(sender,
                                        MessageType.ERROR,
                                        CommandMessageKeys.DATA_NOT_FOUND));
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
				VampireRevamp.syncTaskVPlayer(
						player,
						(vPlayer) -> {
							if (vPlayer.isVampire()) {
								VampireRevamp.sendMessage(sender,
										MessageType.ERROR,
										VampirismMessageKeys.ALREADY_VAMPIRE,
										"{player}", finalPlayer.getDisplayName());
							} else {
								if (!VampireRevamp.getWerewolvesCompat().isWerewolf(finalPlayer)) {
									InfectionReason reason = vPlayer.getReason();
									UUID makerUUID = vPlayer.getMakerUUID();
									if (reason == null) {
										reason = InfectionReason.OPERATOR;
										makerUUID = null;
									}
									vPlayer.setInfection(0);
									vPlayer.addInfection(res, reason, makerUUID);
									vPlayer.update();

									Component attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_INFECTION_ATTRIBUTE);
									Component displayName = Component.text(finalPlayer.getDisplayName());
									Component valComp = Component.text(String.format("%.2f%%", value * 100));
									VampireRevamp.sendMessage(sender,
											MessageType.INFO,
											CommandMessageKeys.SET_CHANGED_VALUE,
											new Tuple<>("{player}", displayName),
											new Tuple<>("{attribute}", attributeName),
											new Tuple<>("{value}", valComp));
								} else {
									VampireRevamp.sendMessage(sender,
											MessageType.ERROR,
											CommandMessageKeys.SET_ERROR_HYBRID,
											"{player}", finalPlayer.getDisplayName());
								}
							}
						},
						(ex) -> VampireRevamp.sendMessage(sender,
                                MessageType.ERROR,
                                CommandMessageKeys.DATA_NOT_FOUND));
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

			Component attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_FOOD_ATTRIBUTE);
			Component displayName = Component.text(player.getDisplayName());
			VampireRevamp.sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					new Tuple<>("{player}", displayName),
					new Tuple<>("{attribute}", attributeName),
					new Tuple<>("{value}", Component.text(String.format("%d", res))));
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

			Component attributeName = VampireRevamp.getMessage(sender, CommandMessageKeys.SET_HEALTH_ATTRIBUTE);
			Component displayName = Component.text(player.getDisplayName());
			VampireRevamp.sendMessage(sender,
					MessageType.INFO,
					CommandMessageKeys.SET_CHANGED_VALUE,
					new Tuple<>("{player}", displayName),
					new Tuple<>("{attribute}", attributeName),
					new Tuple<>("{value}", Component.text(String.format("%d", res))));
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
