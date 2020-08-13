package com.clanjhoo.vampire.config;

import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.util.CollectionUtil;
import com.clanjhoo.vampire.util.SemVer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RadiationConfig {
    public final double opacityPerArmorPiece;
    public final double baseRadiation;
    public final double tempPerRadAndMilli;
    public final RadiationEffectConfig removeBuffs;
    public final List<RadiationEffectConfig> effects;
    public final RadiationEffectConfig burn;
    public final double smokesPerTempAndMilli;
    public final double flamesPerTempAndMilli;
    public final Map<Material, Double> opacity;

    private final transient Double AIR = 0D;
    private final transient Double ALMOST_AIR = 0.1D;
    private final transient Double ICE = 0.75D;
    private final transient Double GLASS_PANE = 0.05D;
    private final transient Double STAINED_GLASS_PANE = ALMOST_AIR;
    private final transient Double GLASS = 0.1D;
    private final transient Double STAINED_GLASS = 0.5D;
    private final transient Double LEAVES = 0.5D;
    private final transient Double ALMOST_BLOCK = 0.9D;
    private final transient Double HALF_BLOCK = 0.5D;
    private final transient Double QUARTER_BLOCK = 0.25D;

    public RadiationConfig() {
        SemVer version = VampireRevamp.getServerVersion();

        opacityPerArmorPiece = 0.125;
        baseRadiation = -0.2;
        tempPerRadAndMilli = 0.0001;
        removeBuffs = new RadiationEffectConfig(true, 0.2, true);
        effects = CollectionUtil.list(
                new RadiationEffectConfig(PotionEffectType.CONFUSION, 0, 0.2, 200, false),
                new RadiationEffectConfig(PotionEffectType.WEAKNESS, 1, 0.3, 200, false),
                new RadiationEffectConfig(PotionEffectType.WEAKNESS, 0, 0.3, 200, true),
                new RadiationEffectConfig(PotionEffectType.SLOW, 0, 0.5, 200, false),
                new RadiationEffectConfig(PotionEffectType.BLINDNESS, 0, 0.8, 200, false)
        );
        burn = new RadiationEffectConfig(true, 0.9, 60, true);
        smokesPerTempAndMilli = 0.012;
        flamesPerTempAndMilli = 0.0004;
        opacity = CollectionUtil.map(
                Material.BARRIER, AIR, //AIR
                Material.BEACON, ALMOST_BLOCK, //AIR
                Material.FROSTED_ICE, ICE,
                Material.GLASS, GLASS,
                Material.ICE, ICE,
                Material.ANVIL, HALF_BLOCK,
                Material.BREWING_STAND, HALF_BLOCK,
                Material.CACTUS, ALMOST_BLOCK,
                Material.CAKE, ALMOST_BLOCK,
                Material.CACTUS, ALMOST_BLOCK,
                Material.CHEST, ALMOST_BLOCK,
                Material.ENDER_CHEST, ALMOST_BLOCK,
                Material.CHORUS_FLOWER, ALMOST_BLOCK,
                Material.CHORUS_PLANT, HALF_BLOCK,
                Material.COCOA, QUARTER_BLOCK,
                Material.DRAGON_EGG, ALMOST_BLOCK,
                Material.END_ROD, ALMOST_AIR,
                Material.ACACIA_FENCE, QUARTER_BLOCK,
                Material.ACACIA_FENCE_GATE, QUARTER_BLOCK,
                Material.BIRCH_FENCE, QUARTER_BLOCK,
                Material.BIRCH_FENCE_GATE, QUARTER_BLOCK,
                Material.DARK_OAK_FENCE, QUARTER_BLOCK,
                Material.DARK_OAK_FENCE_GATE, QUARTER_BLOCK,
                Material.JUNGLE_FENCE, QUARTER_BLOCK,
                Material.JUNGLE_FENCE_GATE, QUARTER_BLOCK,
                Material.SPRUCE_FENCE, QUARTER_BLOCK,
                Material.SPRUCE_FENCE_GATE, QUARTER_BLOCK,
                Material.FLOWER_POT, QUARTER_BLOCK,
                Material.LADDER, ALMOST_AIR,
                Material.DARK_OAK_DOOR, ALMOST_AIR,
                Material.ACACIA_DOOR, ALMOST_AIR,
                Material.BIRCH_DOOR, ALMOST_AIR,
                Material.BIRCH_FENCE_GATE, ALMOST_AIR,
                Material.ACACIA_FENCE_GATE, ALMOST_AIR,
                Material.IRON_DOOR, ALMOST_AIR,
                Material.JUNGLE_DOOR, ALMOST_AIR,
                Material.SPRUCE_DOOR, ALMOST_AIR,
                Material.SPRUCE_FENCE_GATE, ALMOST_AIR,
                Material.JUNGLE_FENCE_GATE, ALMOST_AIR,
                Material.DARK_OAK_FENCE_GATE, ALMOST_AIR,
                Material.IRON_TRAPDOOR, QUARTER_BLOCK,
                Material.TRAPPED_CHEST, ALMOST_BLOCK,
                Material.AIR, AIR, //AIR
                Material.WHEAT, LEAVES,
                Material.FIRE, AIR,
                Material.GRASS, LEAVES,
                Material.BROWN_MUSHROOM, LEAVES,
                Material.RED_MUSHROOM, LEAVES,
                Material.STRUCTURE_VOID, AIR,
                Material.MELON_STEM, LEAVES,
                Material.PUMPKIN_STEM, LEAVES,
                Material.SUGAR_CANE, LEAVES,
                Material.TORCH, ALMOST_AIR,
                Material.VINE, ALMOST_AIR,
                Material.STONE_BUTTON, ALMOST_AIR,
                Material.LEVER, ALMOST_AIR,
                Material.POWERED_RAIL, LEAVES,
                Material.DETECTOR_RAIL, LEAVES,
                Material.ACTIVATOR_RAIL, LEAVES,
                Material.REDSTONE_WIRE, ALMOST_AIR,
                Material.TRIPWIRE, AIR,
                Material.TRIPWIRE_HOOK, ALMOST_AIR,
                Material.WATER, ALMOST_AIR,
                Material.SLIME_BLOCK, ALMOST_BLOCK,
                Material.BEDROCK, STAINED_GLASS
        );

        if (new SemVer(1, 13).compareTo(version) > 0) {
            opacity.put(Material.getMaterial("STAINED_GLASS"), STAINED_GLASS);
            opacity.put(Material.getMaterial("LEAVES"), LEAVES);
            opacity.put(Material.getMaterial("LEAVES_2"), LEAVES);
            opacity.put(Material.getMaterial("WEB"), ALMOST_AIR);
            opacity.put(Material.getMaterial("FENCE"), QUARTER_BLOCK);
            opacity.put(Material.getMaterial("FENCE_GATE"), QUARTER_BLOCK);
            opacity.put(Material.getMaterial("NETHER_FENCE"), QUARTER_BLOCK);
            opacity.put(Material.getMaterial("STAINED_GLASS_PANE"), STAINED_GLASS_PANE);
            opacity.put(Material.getMaterial("SKULL"), HALF_BLOCK);
            opacity.put(Material.getMaterial("WOOD_DOOR"), ALMOST_AIR);
            opacity.put(Material.getMaterial("WOODEN_DOOR"), ALMOST_AIR);
            opacity.put(Material.getMaterial("WATER_LILY"), LEAVES);
            opacity.put(Material.getMaterial("WALL_BANNER"), ALMOST_AIR);
            opacity.put(Material.getMaterial("STANDING_BANNER"), ALMOST_AIR);
            opacity.put(Material.getMaterial("CROPS"), LEAVES);
            opacity.put(Material.getMaterial("YELLOW_FLOWER"), LEAVES);
            opacity.put(Material.getMaterial("RED_ROSE"), LEAVES);
            opacity.put(Material.getMaterial("LONG_GRASS"), LEAVES);
            opacity.put(Material.getMaterial("SAPLING"), LEAVES);
            opacity.put(Material.getMaterial("SIGN_POST"), ALMOST_AIR);
            opacity.put(Material.getMaterial("WOOD_BUTTON"), ALMOST_AIR);
            opacity.put(Material.getMaterial("PORTAL"), STAINED_GLASS_PANE);
            opacity.put(Material.getMaterial("WOOD_PLATE"), ALMOST_BLOCK);
            opacity.put(Material.getMaterial("STONE_PLATE"), ALMOST_BLOCK);
            opacity.put(Material.getMaterial("GOLD_PLATE"), ALMOST_BLOCK);
            opacity.put(Material.getMaterial("IRON_PLATE"), ALMOST_BLOCK);
            opacity.put(Material.getMaterial("REDSTONE_TORCH_OFF"), ALMOST_AIR);
            opacity.put(Material.getMaterial("REDSTONE_TORCH_ON"), ALMOST_AIR);
            opacity.put(Material.getMaterial("RAILS"), LEAVES);
            opacity.put(Material.getMaterial("MOB_SPAWNER"), QUARTER_BLOCK);
        }
        else if (new SemVer(1, 14).compareTo(version) > 0) {
            opacity.put(Material.getMaterial("WALL_SIGN"), ALMOST_AIR);
        }

        if (new SemVer(1, 13).compareTo(version) <= 0) {
            opacity.put(Material.BLACK_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.BLUE_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.BROWN_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.CYAN_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.GRAY_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.GREEN_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.LIGHT_BLUE_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.LIGHT_GRAY_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.LIME_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.MAGENTA_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.ORANGE_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.PINK_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.PURPLE_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.RED_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.WHITE_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.YELLOW_STAINED_GLASS, STAINED_GLASS);
            opacity.put(Material.ACACIA_LEAVES, LEAVES);
            opacity.put(Material.BIRCH_LEAVES, LEAVES);
            opacity.put(Material.DARK_OAK_LEAVES, LEAVES);
            opacity.put(Material.JUNGLE_LEAVES, LEAVES);
            opacity.put(Material.OAK_LEAVES, LEAVES);
            opacity.put(Material.SPRUCE_LEAVES, LEAVES);
            opacity.put(Material.CHIPPED_ANVIL, HALF_BLOCK);
            opacity.put(Material.DAMAGED_ANVIL, HALF_BLOCK);
            opacity.put(Material.COBWEB, ALMOST_AIR);
            opacity.put(Material.CONDUIT, ALMOST_AIR);
            opacity.put(Material.NETHER_BRICK_FENCE, QUARTER_BLOCK);
            opacity.put(Material.OAK_FENCE, QUARTER_BLOCK);
            opacity.put(Material.OAK_FENCE_GATE, QUARTER_BLOCK);
            opacity.put(Material.POTTED_ACACIA_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_ALLIUM, QUARTER_BLOCK);
            opacity.put(Material.POTTED_AZURE_BLUET, QUARTER_BLOCK);
            opacity.put(Material.POTTED_BIRCH_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_BLUE_ORCHID, QUARTER_BLOCK);
            opacity.put(Material.POTTED_BROWN_MUSHROOM, QUARTER_BLOCK);
            opacity.put(Material.POTTED_CACTUS, QUARTER_BLOCK);
            opacity.put(Material.POTTED_DANDELION, QUARTER_BLOCK);
            opacity.put(Material.POTTED_DARK_OAK_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_DEAD_BUSH, QUARTER_BLOCK);
            opacity.put(Material.POTTED_FERN, QUARTER_BLOCK);
            opacity.put(Material.POTTED_JUNGLE_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_OAK_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_ORANGE_TULIP, QUARTER_BLOCK);
            opacity.put(Material.POTTED_OXEYE_DAISY, QUARTER_BLOCK);
            opacity.put(Material.POTTED_PINK_TULIP, QUARTER_BLOCK);
            opacity.put(Material.POTTED_POPPY, QUARTER_BLOCK);
            opacity.put(Material.POTTED_RED_MUSHROOM, QUARTER_BLOCK);
            opacity.put(Material.POTTED_RED_TULIP, QUARTER_BLOCK);
            opacity.put(Material.POTTED_SPRUCE_SAPLING, QUARTER_BLOCK);
            opacity.put(Material.POTTED_WHITE_TULIP, QUARTER_BLOCK);
            opacity.put(Material.COBBLESTONE_WALL, HALF_BLOCK);
            opacity.put(Material.MOSSY_COBBLESTONE_WALL, HALF_BLOCK);
            opacity.put(Material.GLASS_PANE, GLASS_PANE);
            opacity.put(Material.BLACK_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.BLUE_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.BROWN_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.CYAN_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.GRAY_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.GREEN_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.LIGHT_BLUE_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.LIGHT_GRAY_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.LIME_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.MAGENTA_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.ORANGE_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.PINK_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.PURPLE_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.RED_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.WHITE_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.YELLOW_STAINED_GLASS_PANE, STAINED_GLASS_PANE);
            opacity.put(Material.SKELETON_SKULL, HALF_BLOCK);
            opacity.put(Material.SKELETON_WALL_SKULL, HALF_BLOCK);
            opacity.put(Material.WITHER_SKELETON_SKULL, HALF_BLOCK);
            opacity.put(Material.WITHER_SKELETON_WALL_SKULL, HALF_BLOCK);
            opacity.put(Material.CREEPER_HEAD, HALF_BLOCK);
            opacity.put(Material.CREEPER_WALL_HEAD, HALF_BLOCK);
            opacity.put(Material.DRAGON_HEAD, HALF_BLOCK);
            opacity.put(Material.DRAGON_WALL_HEAD, HALF_BLOCK);
            opacity.put(Material.PLAYER_HEAD, HALF_BLOCK);
            opacity.put(Material.PLAYER_WALL_HEAD, HALF_BLOCK);
            opacity.put(Material.ZOMBIE_HEAD, HALF_BLOCK);
            opacity.put(Material.ZOMBIE_WALL_HEAD, HALF_BLOCK);
            opacity.put(Material.IRON_BARS, ALMOST_AIR);
            opacity.put(Material.LILY_PAD, LEAVES);
            opacity.put(Material.SEA_PICKLE, LEAVES);
            opacity.put(Material.TURTLE_EGG, LEAVES);
            opacity.put(Material.OAK_DOOR, ALMOST_AIR);
            opacity.put(Material.PISTON_HEAD, QUARTER_BLOCK);
            opacity.put(Material.ACACIA_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.BIRCH_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.DARK_OAK_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.JUNGLE_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.OAK_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.SPRUCE_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.CAVE_AIR, AIR);
            opacity.put(Material.VOID_AIR, AIR);
            opacity.put(Material.BLACK_BANNER, ALMOST_AIR);
            opacity.put(Material.BLACK_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.BLUE_BANNER, ALMOST_AIR);
            opacity.put(Material.BLUE_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.BROWN_BANNER, ALMOST_AIR);
            opacity.put(Material.BROWN_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.CYAN_BANNER, ALMOST_AIR);
            opacity.put(Material.CYAN_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.GRAY_BANNER, ALMOST_AIR);
            opacity.put(Material.GRAY_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.GREEN_BANNER, ALMOST_AIR);
            opacity.put(Material.GREEN_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.LIGHT_BLUE_BANNER, ALMOST_AIR);
            opacity.put(Material.LIGHT_BLUE_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.LIGHT_GRAY_BANNER, ALMOST_AIR);
            opacity.put(Material.LIGHT_GRAY_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.LIME_BANNER, ALMOST_AIR);
            opacity.put(Material.LIME_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.MAGENTA_BANNER, ALMOST_AIR);
            opacity.put(Material.MAGENTA_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.ORANGE_BANNER, ALMOST_AIR);
            opacity.put(Material.ORANGE_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.PINK_BANNER, ALMOST_AIR);
            opacity.put(Material.PINK_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.PURPLE_BANNER, ALMOST_AIR);
            opacity.put(Material.PURPLE_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.RED_BANNER, ALMOST_AIR);
            opacity.put(Material.RED_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.WHITE_BANNER, ALMOST_AIR);
            opacity.put(Material.WHITE_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.YELLOW_BANNER, ALMOST_AIR);
            opacity.put(Material.YELLOW_WALL_BANNER, ALMOST_AIR);
            opacity.put(Material.BRAIN_CORAL, LEAVES);
            opacity.put(Material.BRAIN_CORAL_FAN, LEAVES);
            opacity.put(Material.BRAIN_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.DEAD_BRAIN_CORAL, LEAVES);
            opacity.put(Material.DEAD_BRAIN_CORAL_FAN, LEAVES);
            opacity.put(Material.DEAD_BRAIN_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.BUBBLE_CORAL, LEAVES);
            opacity.put(Material.BUBBLE_CORAL_FAN, LEAVES);
            opacity.put(Material.BUBBLE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.DEAD_BUBBLE_CORAL, LEAVES);
            opacity.put(Material.DEAD_BUBBLE_CORAL_FAN, LEAVES);
            opacity.put(Material.DEAD_BUBBLE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.FIRE_CORAL, LEAVES);
            opacity.put(Material.FIRE_CORAL_FAN, LEAVES);
            opacity.put(Material.FIRE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.DEAD_FIRE_CORAL, LEAVES);
            opacity.put(Material.DEAD_FIRE_CORAL_FAN, LEAVES);
            opacity.put(Material.DEAD_FIRE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.HORN_CORAL, LEAVES);
            opacity.put(Material.HORN_CORAL_FAN, LEAVES);
            opacity.put(Material.HORN_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.DEAD_HORN_CORAL, LEAVES);
            opacity.put(Material.DEAD_HORN_CORAL_FAN, LEAVES);
            opacity.put(Material.DEAD_HORN_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.TUBE_CORAL, LEAVES);
            opacity.put(Material.TUBE_CORAL_FAN, LEAVES);
            opacity.put(Material.TUBE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.DEAD_TUBE_CORAL, LEAVES);
            opacity.put(Material.DEAD_TUBE_CORAL_FAN, LEAVES);
            opacity.put(Material.DEAD_TUBE_CORAL_WALL_FAN, LEAVES);
            opacity.put(Material.BEETROOTS, LEAVES);
            opacity.put(Material.CARROTS, LEAVES);
            opacity.put(Material.POTATOES, LEAVES);
            opacity.put(Material.FERN, LEAVES);
            opacity.put(Material.DANDELION, LEAVES);
            opacity.put(Material.POPPY, LEAVES);
            opacity.put(Material.BLUE_ORCHID, LEAVES);
            opacity.put(Material.ALLIUM, LEAVES);
            opacity.put(Material.AZURE_BLUET, LEAVES);
            opacity.put(Material.RED_TULIP, LEAVES);
            opacity.put(Material.ORANGE_TULIP, LEAVES);
            opacity.put(Material.WHITE_TULIP, LEAVES);
            opacity.put(Material.PINK_TULIP, LEAVES);
            opacity.put(Material.OXEYE_DAISY, LEAVES);
            opacity.put(Material.SUNFLOWER, LEAVES);
            opacity.put(Material.LILAC, LEAVES);
            opacity.put(Material.ROSE_BUSH, LEAVES);
            opacity.put(Material.PEONY, LEAVES);
            opacity.put(Material.TALL_GRASS, LEAVES);
            opacity.put(Material.KELP_PLANT, LEAVES);
            opacity.put(Material.NETHER_PORTAL, STAINED_GLASS_PANE);
            opacity.put(Material.NETHER_WART, LEAVES);
            opacity.put(Material.ACACIA_SAPLING, LEAVES);
            opacity.put(Material.BIRCH_SAPLING, LEAVES);
            opacity.put(Material.DARK_OAK_SAPLING, LEAVES);
            opacity.put(Material.JUNGLE_SAPLING, LEAVES);
            opacity.put(Material.OAK_SAPLING, LEAVES);
            opacity.put(Material.SPRUCE_SAPLING, LEAVES);
            opacity.put(Material.SEAGRASS, LEAVES);
            opacity.put(Material.TALL_SEAGRASS, LEAVES);
            opacity.put(Material.ATTACHED_MELON_STEM, LEAVES);
            opacity.put(Material.ATTACHED_PUMPKIN_STEM, LEAVES);
            opacity.put(Material.WALL_TORCH, ALMOST_AIR);
            opacity.put(Material.ACACIA_BUTTON, ALMOST_AIR);
            opacity.put(Material.BIRCH_BUTTON, ALMOST_AIR);
            opacity.put(Material.DARK_OAK_BUTTON, ALMOST_AIR);
            opacity.put(Material.JUNGLE_BUTTON, ALMOST_AIR);
            opacity.put(Material.OAK_BUTTON, ALMOST_AIR);
            opacity.put(Material.SPRUCE_BUTTON, ALMOST_AIR);
            opacity.put(Material.ACACIA_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.BIRCH_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.DARK_OAK_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.JUNGLE_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.OAK_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.SPRUCE_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.STONE_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.RAIL, LEAVES);
            opacity.put(Material.REDSTONE_TORCH, ALMOST_AIR);
            opacity.put(Material.REDSTONE_WALL_TORCH, ALMOST_AIR);
            opacity.put(Material.BUBBLE_COLUMN, ALMOST_AIR);
            opacity.put(Material.SPAWNER, QUARTER_BLOCK);
        }

        if (new SemVer(1, 14).compareTo(version) <= 0) {
            opacity.put(Material.BAMBOO, QUARTER_BLOCK);
            opacity.put(Material.BELL, QUARTER_BLOCK);
            opacity.put(Material.POTTED_BAMBOO, QUARTER_BLOCK);
            opacity.put(Material.POTTED_CORNFLOWER, QUARTER_BLOCK);
            opacity.put(Material.POTTED_LILY_OF_THE_VALLEY, QUARTER_BLOCK);
            opacity.put(Material.POTTED_WITHER_ROSE, QUARTER_BLOCK);
            opacity.put(Material.ANDESITE_WALL, HALF_BLOCK);
            opacity.put(Material.BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.DIORITE_WALL, HALF_BLOCK);
            opacity.put(Material.END_STONE_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.GRANITE_WALL, HALF_BLOCK);
            opacity.put(Material.MOSSY_STONE_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.NETHER_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.PRISMARINE_WALL, HALF_BLOCK);
            opacity.put(Material.RED_NETHER_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.RED_SANDSTONE_WALL, HALF_BLOCK);
            opacity.put(Material.SANDSTONE_WALL, HALF_BLOCK);
            opacity.put(Material.STONE_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.GRINDSTONE, HALF_BLOCK);
            opacity.put(Material.LANTERN, QUARTER_BLOCK);
            opacity.put(Material.LECTERN, ALMOST_BLOCK);
            opacity.put(Material.SCAFFOLDING, STAINED_GLASS);
            opacity.put(Material.SWEET_BERRY_BUSH, LEAVES);
            opacity.put(Material.CORNFLOWER, LEAVES);
            opacity.put(Material.LILY_OF_THE_VALLEY, LEAVES);
            opacity.put(Material.WITHER_ROSE, LEAVES);
            opacity.put(Material.BAMBOO_SAPLING, LEAVES);
            opacity.put(Material.ACACIA_SIGN, ALMOST_AIR);
            opacity.put(Material.ACACIA_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.BIRCH_SIGN, ALMOST_AIR);
            opacity.put(Material.BIRCH_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.DARK_OAK_SIGN, ALMOST_AIR);
            opacity.put(Material.DARK_OAK_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.JUNGLE_SIGN, ALMOST_AIR);
            opacity.put(Material.JUNGLE_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.OAK_SIGN, ALMOST_AIR);
            opacity.put(Material.OAK_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.SPRUCE_SIGN, ALMOST_AIR);
            opacity.put(Material.SPRUCE_WALL_SIGN, ALMOST_AIR);
        }

        if (new SemVer(1, 15).compareTo(version) <= 0) {
            opacity.put(Material.HONEY_BLOCK, ALMOST_BLOCK);
        }

        if (new SemVer(1, 16).compareTo(version) <= 0) {
            opacity.put(Material.CHAIN, ALMOST_AIR);
            opacity.put(Material.CRIMSON_FENCE, QUARTER_BLOCK);
            opacity.put(Material.CRIMSON_FENCE_GATE, QUARTER_BLOCK);
            opacity.put(Material.WARPED_FENCE, QUARTER_BLOCK);
            opacity.put(Material.WARPED_FENCE_GATE, QUARTER_BLOCK);
            opacity.put(Material.POTTED_CRIMSON_FUNGUS, QUARTER_BLOCK);
            opacity.put(Material.POTTED_CRIMSON_ROOTS, QUARTER_BLOCK);
            opacity.put(Material.POTTED_WARPED_FUNGUS, QUARTER_BLOCK);
            opacity.put(Material.POTTED_WARPED_ROOTS, QUARTER_BLOCK);
            opacity.put(Material.BLACKSTONE_WALL, HALF_BLOCK);
            opacity.put(Material.POLISHED_BLACKSTONE_BRICK_WALL, HALF_BLOCK);
            opacity.put(Material.POLISHED_BLACKSTONE_WALL, HALF_BLOCK);
            opacity.put(Material.CRIMSON_DOOR, ALMOST_AIR);
            opacity.put(Material.WARPED_DOOR, ALMOST_AIR);
            opacity.put(Material.CRIMSON_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.WARPED_TRAPDOOR, QUARTER_BLOCK);
            opacity.put(Material.SOUL_FIRE, AIR);
            opacity.put(Material.CRIMSON_FUNGUS, LEAVES);
            opacity.put(Material.WARPED_FUNGUS, LEAVES);
            opacity.put(Material.NETHER_SPROUTS, LEAVES);
            opacity.put(Material.CRIMSON_ROOTS, LEAVES);
            opacity.put(Material.WARPED_ROOTS, LEAVES);
            opacity.put(Material.CRIMSON_SIGN, ALMOST_AIR);
            opacity.put(Material.CRIMSON_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.WARPED_SIGN, ALMOST_AIR);
            opacity.put(Material.WARPED_WALL_SIGN, ALMOST_AIR);
            opacity.put(Material.SOUL_TORCH, ALMOST_AIR);
            opacity.put(Material.SOUL_WALL_TORCH, ALMOST_AIR);
            opacity.put(Material.TWISTING_VINES_PLANT, ALMOST_AIR);
            opacity.put(Material.WEEPING_VINES_PLANT, ALMOST_AIR);
            opacity.put(Material.CRIMSON_BUTTON, ALMOST_AIR);
            opacity.put(Material.POLISHED_BLACKSTONE_BUTTON, ALMOST_AIR);
            opacity.put(Material.WARPED_BUTTON, ALMOST_AIR);
            opacity.put(Material.CRIMSON_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, ALMOST_BLOCK);
            opacity.put(Material.WARPED_PRESSURE_PLATE, ALMOST_BLOCK);
        }
    }

    public RadiationConfig(@Nonnull ConfigurationSection cs) {
        RadiationConfig def = new RadiationConfig();

        opacityPerArmorPiece = cs.getDouble("opacityPerArmorPiece", def.opacityPerArmorPiece);
        baseRadiation = cs.getDouble("baseRadiation", def.baseRadiation);
        tempPerRadAndMilli = cs.getDouble("tempPerRadAndMilli", def.tempPerRadAndMilli);
        boolean remEnabled = cs.getBoolean("removeBuffs.enabled", def.removeBuffs.enabled);
        double remTemp = cs.getDouble("removeBuffs.temperature", def.removeBuffs.temperature);
        boolean remNosf = cs.getBoolean("removeBuffs.affectNosferatu", def.removeBuffs.affectNosferatu);
        removeBuffs = new RadiationEffectConfig(remEnabled, remTemp, remNosf);

        List<Map<?, ?>> auxLEff;
        List<RadiationEffectConfig> effs = null;
        if (cs.contains("effects")) {
            auxLEff = cs.getMapList("effects");
            effs = new LinkedList<>();
            for (Map<?, ?> minimap : auxLEff) {
                PotionEffectType type = null;
                Integer str = null;
                Double temp = null;
                Integer ticks = null;
                Boolean an = null;

                for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                    String key = (String) entry.getKey();
                    if (key.equals("type")) {
                        String typeName = (String) entry.getValue();
                        type = PotionEffectType.getByName(typeName);

                        if (type == null) {
                            VampireRevamp.log(Level.WARNING, "PotionEffectType " + typeName + " doesn't exist!");
                            break;
                        }
                    }
                    else if (key.equals("strength")) {
                        str = (Integer) entry.getValue();
                    }
                    else if (key.equals("temperature")) {
                        temp = (Double) entry.getValue();

                        if (temp < 0 || temp > 1) {
                            VampireRevamp.log(Level.WARNING, "Temperature must be between 0 and 1 doesn't exist!");
                            temp = null;
                            break;
                        }
                    }
                    else if (key.equals("ticks")) {
                        ticks = (Integer) entry.getValue();

                        if (ticks < 1) {
                            VampireRevamp.log(Level.WARNING, "Ticks must be positive non 0!");
                            ticks = null;
                            break;
                        }
                    }
                    else if (key.equals("affectNosferatu")) {
                        an = (Boolean) entry.getValue();
                    }
                }

                if (type == null || temp == null || ticks == null || an == null) {
                    VampireRevamp.log(Level.WARNING, "You have to specify a valid type, temperature, ticks and affectNosferatu for all effects!");
                }
                else {
                    if (str == null)
                        str = 0;
                    effs.add(new RadiationEffectConfig(type, str, temp, ticks, an));
                }
            }
        }
        effects = effs != null ? effs : def.effects;

        remEnabled = cs.getBoolean("burn.enabled", def.burn.enabled);
        remTemp = cs.getDouble("burn.temperature", def.burn.temperature);
        int remTicks = cs.getInt("burn.ticks", def.burn.ticks);
        remNosf = cs.getBoolean("burn.affectNosferatu", def.burn.affectNosferatu);
        burn = new RadiationEffectConfig(remEnabled, remTemp, remTicks, remNosf);
        smokesPerTempAndMilli = cs.getDouble("smokesPerTempAndMilli", def.smokesPerTempAndMilli);
        flamesPerTempAndMilli = cs.getDouble("flamesPerTempAndMilli", def.flamesPerTempAndMilli);


        auxLEff = null;
        Map<Material, Double> auxOp = null;
        if (cs.contains("opacity")) {
            auxLEff = cs.getMapList("opacity");
            auxOp = new HashMap<>();
            for (Map<?, ?> minimap : auxLEff) {
                for (Map.Entry<?, ?> entry : minimap.entrySet()) {
                    Material mat = Material.matchMaterial((String) entry.getKey());

                    if (mat != null) {
                        double occlusion = -1;
                        if (entry.getValue() instanceof Number)
                            occlusion = ((Number) entry.getValue()).doubleValue();

                        if (occlusion < 0 || occlusion > 1) {
                            VampireRevamp.log(Level.WARNING, "Occlusion value must be between 0 and 1 (both inclusive)");
                        }
                        else if (mat.isOccluding() && !def.opacity.containsKey(mat)){
                            VampireRevamp.log(Level.WARNING, "Occlusion value ignored due to " + mat.name() + " being occlusive. If you believe this is an error, contact us in the Spigot discussion.");
                        }
                        else {
                            auxOp.put(mat, occlusion);
                        }
                    }
                }
            }
            if (auxOp.size() == 0) {
                auxOp = null;
                VampireRevamp.log(Level.WARNING, "Occlusion values restored to default values");
            }
        }
        opacity = auxOp != null ? auxOp : def.opacity;
    }

    protected boolean saveConfigToFile(BufferedWriter configWriter, String indent, int level) {
        boolean result = PluginConfig.writeLine(configWriter, "# Opacity added by each piece of armor worn by a player", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "opacityPerArmorPiece: " + this.opacityPerArmorPiece, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Radiation value when Sun is totally blocked. Higher negative values make the temperature decrease faster when not under the sun. Positive values is like setting eternal sunlight everywhere", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "baseRadiation: " + this.baseRadiation, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "# Temperature added per radiation point each millisecond", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "tempPerRadAndMilli: " + this.tempPerRadAndMilli, indent, level);

        List<String> auxData = removeBuffs.getData();
        result = result && PluginConfig.writeLine(configWriter, "# Buffs will be removed from the specified types of vampires when specified temperature is reached", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "removeBuffs:", indent, level);
        for (int i = 0; i < auxData.size(); i++)
            result = result && PluginConfig.writeLine(configWriter, auxData.get(i), indent, level + 1);

        result = result && PluginConfig.writeLine(configWriter, "effects:", indent, level);
        for (RadiationEffectConfig effectConfig : effects) {
            auxData = effectConfig.getData();
            result = result && PluginConfig.writeLine(configWriter, "- " + auxData.get(0), indent, level + 1);
            for (int i = 1; i < auxData.size(); i++)
                result = result && PluginConfig.writeLine(configWriter, "  " + auxData.get(i), indent, level + 1);
        }

        auxData = burn.getData();
        result = result && PluginConfig.writeLine(configWriter, "# Vampires will start burning when specified temperature is reached", indent, level);
        result = result && PluginConfig.writeLine(configWriter, "burn:", indent, level);
        for (int i = 0; i < auxData.size(); i++)
            result = result && PluginConfig.writeLine(configWriter, auxData.get(i), indent, level + 1);

        result = result && PluginConfig.writeLine(configWriter, "smokesPerTempAndMilli: " + this.smokesPerTempAndMilli, indent, level);
        result = result && PluginConfig.writeLine(configWriter, "flamesPerTempAndMilli: " + this.flamesPerTempAndMilli, indent, level);
        result = result && PluginConfig.writeMap(configWriter, "opacity:",  this.opacity, indent, level);

        return result;
    }

    @Override
    public String toString() {
        return "RadiationConfig{" +
                "opacityPerArmorPiece=" + opacityPerArmorPiece +
                ", baseRadiation=" + baseRadiation +
                ", tempPerRadAndMilli=" + tempPerRadAndMilli +
                ", removeBuffs=" + removeBuffs +
                ", effects=" + effects +
                ", burn=" + burn +
                ", smokesPerTempAndMilli=" + smokesPerTempAndMilli +
                ", flamesPerTempAndMilli=" + flamesPerTempAndMilli +
                ", opacity=" + opacity +
                '}';
    }
}
