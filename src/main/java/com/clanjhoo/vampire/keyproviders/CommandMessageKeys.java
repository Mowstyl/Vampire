package com.clanjhoo.vampire.keyproviders;


import java.util.Locale;

public enum CommandMessageKeys implements KeyProvider {
    COMMAND_HELP_HEADER("commands.commandHelpHeader"),
    NO_PLAYER_FOUND("commands.noPlayerFound"),
    BLACKLISTED_WORLD("commands.blacklistedWorld"),
    NOT_ENOUGH_INPUTS("commands.notEnoughInputs"),
    NOT_ENOUGH_PERMS("commands.notEnoughPerms"),
    NOT_VALID_VALUE("commands.notValidValue"),
    DATA_NOT_FOUND("commands.dataNotFound"),
    FULL_INVENTORY("commands.fullInventory"),
    DISABLED_ACTION("commands.disabledAction"),
    HELP_DESCRIPTION("commands.help_description"),
    RELOAD_DESCRIPTION("commands.reload_description"),
    RELOAD_SUCCESS("commands.reload_success"),
    RELOAD_FAIL("commands.reload_fail"),
    SHOW_DESCRIPTION("commands.show_description"),
    SHOW_TYPE("commands.show_type"),
    SHOW_BLOODLUST("commands.show_bloodlust"),
    SHOW_INTENT("commands.show_intent"),
    SHOW_NIGHTVISION("commands.show_nightvision"),
    SHOW_TEMPERATURE("commands.show_temperature"),
    SHOW_RADIATION_KEYS("commands.show_radiationKeys"),
    SHOW_RADIATION_VALUES("commands.show_radiationValues"),
    SHOW_INFECTED("commands.show_infected"),
    SHOW_CURED("commands.show_cured"),
    BLOODLUST_DESCRIPTION("commands.bloodlust_description"),
    INTENT_DESCRIPTION("commands.intent_description"),
    NIGHTVISION_DESCRIPTION("commands.nightvision_description"),
    OFFER_DESCRIPTION("commands.offer_description"),
    OFFER_INVALID_VALUE("commands.offer_description"),
    ACCEPT_DESCRIPTION("commands.accept_description"),
    FLASK_DESCRIPTION("commands.flask_description"),
    SHRIEK_DESCRIPTION("commands.shriek_description"),
    LIST_DESCRIPTION("commands.list_description"),
    VERSION_DESCRIPTION("commands.version_description"),
    BATUSI_DESCRIPTION("commands.batusi_description"),
    BATUSI_TOGGLED_ON("commands.batusi_toggledOn"),
    BATUSI_TOGGLED_OFF("commands.batusi_toggledOff"),
    BATUSI_ALREADY_USED("commands.batusi_alreadyUsed"),
    BATUSI_DEFVALUE_ERROR("commands.batusi_defvalue_error"),
    BATUSI_NEGATIVE_BATS("commands.batusi_negative_bats"),
    BATUSI_TOO_MANY("commands.batusi_too_many"),
    BATUSI_IGNORED_BATS("commands.batusi_ignored_bats"),
    BATUSI_ERROR("commands.batusi_error"),
    RING_DESCRIPTION("commands.ring_description"),
    BASE_DESCRIPTION("commands.base_description"),
    SET_DESCRIPTION("commands.set_description"),
    SET_ERROR_HYBRID("commands.set_errorHybrid"),
    SET_CHANGED_VALUE("commands.set_changedValue"),
    SET_HELP_DESCRIPTION("commands.set_description"),
    SET_VAMPIRE_DESCRIPTION("commands.set_vampire_description"),
    SET_VAMPIRE_ATTRIBUTE("commands.set_vampire_attribute"),
    SET_NOSFERATU_DESCRIPTION("commands.set_nosferatu_description"),
    SET_NOSFERATU_ATTRIBUTE("commands.set_nosferatu_attribute"),
    SET_INFECTION_DESCRIPTION("commands.set_infection_description"),
    SET_INFECTION_ATTRIBUTE("commands.set_infection_attribute"),
    SET_FOOD_DESCRIPTION("commands.set_food_description"),
    SET_FOOD_ATTRIBUTE("commands.set_food_attribute"),
    SET_HEALTH_DESCRIPTION("commands.set_health_description"),
    SET_HEALTH_ATTRIBUTE("commands.set_health_attribute"),
    HOLYWATER_DESCRIPTION("commands.holywater_description"),
    UNKNOWN_DESCRIPTION("commands.unknown_description")
    ;

    CommandMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }

    public static CommandMessageKeys getProviderFromCommand(String commandName) {

        //VampireRevamp.debugLog(Level.INFO, commandName);

        return switch (commandName) {
            case "help" -> HELP_DESCRIPTION;
            case "reload" -> RELOAD_DESCRIPTION;
            case "list" -> LIST_DESCRIPTION;
            case "version" -> VERSION_DESCRIPTION;
            case "show" -> SHOW_DESCRIPTION;
            case "bloodlust" -> BLOODLUST_DESCRIPTION;
            case "nightvision" -> NIGHTVISION_DESCRIPTION;
            case "intend" -> INTENT_DESCRIPTION;
            case "offer" -> OFFER_DESCRIPTION;
            case "accept" -> ACCEPT_DESCRIPTION;
            case "flask" -> FLASK_DESCRIPTION;
            case "shriek" -> SHRIEK_DESCRIPTION;
            case "batusi" -> BATUSI_DESCRIPTION;
            case "holywater" -> HOLYWATER_DESCRIPTION;
            case "ring" -> RING_DESCRIPTION;
            case "set help" -> SET_DESCRIPTION;
            case "set vampire" -> SET_VAMPIRE_DESCRIPTION;
            case "set nosferatu" -> SET_NOSFERATU_DESCRIPTION;
            case "set infection" -> SET_INFECTION_DESCRIPTION;
            case "set health" -> SET_HEALTH_DESCRIPTION;
            case "set food" -> SET_FOOD_DESCRIPTION;
            default -> UNKNOWN_DESCRIPTION;
        };
    }
}
