package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.InfectionReason;
import com.clanjhoo.vampire.VampireRevamp;
import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.entity.UPlayerColl;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VampireExpansion extends PlaceholderExpansion {
    private final VampireRevamp plugin;

    public VampireExpansion() {
        plugin = VampireRevamp.getInstance();
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
     *
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

        UPlayer uplayer = UPlayerColl.get(player.getUniqueId());

        // %vampire_temperature%
        if(identifier.equals("temperature")){
            return String.format("%f", uplayer.getTemp());
        }

        // %vampire_radiation%
        if(identifier.equals("radiation")){
            return String.format("%f", uplayer.getRad());
        }

        // %vampire_infectionlevel%
        if(identifier.equals("infectionlevel")){
            return String.format("%f", uplayer.getInfection());
        }

        // %vampire_maker%
        if(identifier.equals("maker")){
            String name = null;

            if (uplayer.isHuman()) {
                name = "None";
            }
            else {
                try {
                    name = uplayer.getMakerName();
                } catch (Exception ignored) {

                }

                if (name == null) {
                    InfectionReason reason = uplayer.getReason();
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
        }

        // %vampire_bloodlust%
        if(identifier.equals("bloodlust")){
            return String.format("%b", uplayer.isBloodlusting());
        }

        // %vampire_intend%
        if(identifier.equals("intend")){
            return String.format("%b", uplayer.isIntending());
        }

        // %vampire_nightvision%
        if(identifier.equals("nightvision")){
            return String.format("%b", uplayer.isUsingNightVision());
        }

        // %vampire_batusi%
        if(identifier.equals("batusi")){
            return String.format("%b", uplayer.isBatusi());
        }

        // %vampire_healthy%
        if(identifier.equals("healthy")){
            return String.format("%b", uplayer.isHealthy());
        }

        // %vampire_unhealthy%
        if(identifier.equals("unhealthy")){
            return String.format("%b", uplayer.isUnhealthy());
        }

        // %vampire_human%
        if(identifier.equals("human")){
            return String.format("%b", uplayer.isHuman());
        }

        // %vampire_infected%
        if(identifier.equals("infected")){
            return String.format("%b", uplayer.isInfected());
        }

        // %vampire_vampire%
        if(identifier.equals("vampire")){
            return String.format("%b", uplayer.isVampire());
        }

        // %vampire_nosferatu%
        if(identifier.equals("nosferatu")){
            return String.format("%b", uplayer.isNosferatu());
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
