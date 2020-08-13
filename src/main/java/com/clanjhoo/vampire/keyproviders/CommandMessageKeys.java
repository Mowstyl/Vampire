package com.clanjhoo.vampire.keyproviders;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum CommandMessageKeys implements MessageKeyProvider {
    COMMAND_HELP_HEADER("commands.commandHelpHeader"),
    NO_PLAYER_FOUND("commands.noPlayerFound"),
    BLACKLISTED_WORLD("commands.blacklistedWorld"),
    NOT_ENOUGH_INPUTS("commands.notEnoughInputs"),
    NOT_ENOUGH_PERMS("commands.notEnoughPerms"),
    NOT_VALID_VALUE("commands.notValidValue"),
    DATA_NOT_FOUND("commands.dataNotFound"),
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
    BATUSI_ERROR("commands.batusi_error"),
    SET_DESCRIPTION("commands.set_description"),
    SET_ERROR_HYBRID("commands.set_errorHybrid"),
    SET_CHANGED_VALUE("commands.set_changedValue"),
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
    UNKNOWN_DESCRIPTION("commands.unknown_description")
    ;

    CommandMessageKeys(String key) {
        this.key = MessageKey.of(key);
    }

    private final MessageKey key;

    @Override
    public MessageKey getMessageKey() {
        return key;
    }

    public static CommandMessageKeys getProviderFromCommand(String commandName) {
        CommandMessageKeys result;

        switch (commandName) {
            case "help":
                result = HELP_DESCRIPTION;
                break;
            case "reload":
                result = RELOAD_DESCRIPTION;
                break;
            case "list":
                result = LIST_DESCRIPTION;
                break;
            case "version":
                result = VERSION_DESCRIPTION;
                break;
            case "show":
                result = SHOW_DESCRIPTION;
                break;
            case "bloodlust":
                result = BLOODLUST_DESCRIPTION;
                break;
            case "nightvision":
                result = NIGHTVISION_DESCRIPTION;
                break;
            case "intend":
                result = INTENT_DESCRIPTION;
                break;
            case "offer":
                result = OFFER_DESCRIPTION;
                break;
            case "accept":
                result = ACCEPT_DESCRIPTION;
                break;
            case "flask":
                result = FLASK_DESCRIPTION;
                break;
            case "shriek":
                result = SHRIEK_DESCRIPTION;
                break;
            case "batusi":
                result = BATUSI_DESCRIPTION;
                break;
            case "set help":
                result = SET_DESCRIPTION;
                break;
            case "set vampire":
                result = SET_VAMPIRE_DESCRIPTION;
                break;
            case "set nosferatu":
                result = SET_NOSFERATU_DESCRIPTION;
                break;
            case "set infection":
                result = SET_INFECTION_DESCRIPTION;
                break;
            case "set health":
                result = SET_HEALTH_DESCRIPTION;
                break;
            case "set food":
                result = SET_FOOD_DESCRIPTION;
                break;
            default:
                result = UNKNOWN_DESCRIPTION;
        }

        //VampireRevamp.debugLog(Level.INFO, commandName);

        return result;
    }
}
