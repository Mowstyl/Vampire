package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.VPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VampireExpansion extends PlaceholderExpansion {
    private final VampireRevamp plugin;

    public VampireExpansion(VampireRevamp plugin) {
        this.plugin = plugin;
    }
    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public @NotNull String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "vampire";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public @NotNull String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.entity.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null){
            return "";
        }

        VPlayer vPlayer = plugin.getVPlayer(player);
        if (vPlayer == null){
            return "";
        }

        switch (identifier) {
            // %vampire_temperature%
            case "temperature":
                return String.format("%f", vPlayer.getTemp());
            // %vampire_radiation%
            case "radiation":
                return String.format("%f", vPlayer.getRad());
            // %vampire_infectionlevel%
            case "infectionlevel":
                return String.format("%f", vPlayer.getInfection());
            // %vampire_maker%
            case "maker":
                String name = null;
                if (vPlayer.isHuman()) {
                    name = "None";
                }
                else {
                    name = vPlayer.getMakerName();
                    if (name == null) {
                        InfectionReason reason = vPlayer.getReason();
                        switch (reason) {
                            case UNKNOWN:
                            case COMBAT_INTENDED:
                            case COMBAT_MISTAKE:
                            case TRADE:
                                name = "Unknown";
                                break;
                            case FLASK:
                                name = "Blood flask";
                                break;
                            case OPERATOR:
                                name = "Dark magic";
                                break;
                            case ALTAR:
                                name = "Altar of darkness";
                                break;
                        }
                    }
                }
                return name;
            // %vampire_bloodlust%
            case "bloodlust":
                return String.format("%b", vPlayer.isBloodlusting());
            // %vampire_intend%
            case "intend":
                return String.format("%b", vPlayer.isIntending());
            // %vampire_nightvision%
            case "nightvision":
                return String.format("%b", vPlayer.isUsingNightVision());
            // %vampire_batusi%
            case "batusi":
                return String.format("%b", vPlayer.isBatusi());
            // %vampire_healthy%
            case "healthy":
                return String.format("%b", vPlayer.isHealthy());
            // %vampire_unhealthy%
            case "unhealthy":
                return String.format("%b", vPlayer.isUnhealthy());
            // %vampire_human%
            case "human":
                return String.format("%b", vPlayer.isHuman());
            // %vampire_infected%
            case "infected":
                return String.format("%b", vPlayer.isInfected());
            // %vampire_vampire%
            case "vampire":
                return String.format("%b", vPlayer.isVampire());
            // %vampire_nosferatu%
            case "nosferatu":
                return String.format("%b", vPlayer.isNosferatu());
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }

    @Override
    public int hashCode() {
        String hashable = getAuthor() + ":" + getIdentifier() + ":" + getVersion();
        return hashable.hashCode() + 6470;
    }
}
