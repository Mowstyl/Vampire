package com.clanjhoo.vampire;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Perm {
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	BASECOMMAND,

	HELP,
	SHOW,
	SHOW_OTHER,
	SHRIEK,
	MODE_BLOODLUST,
	MODE_INTENT,
	MODE_NIGHTVISION,
	MODE_BATUSI,
	MODE_BATUSI_DISGUISE,
	TRADE_OFFER,
	TRADE_ACCEPT,
	TRADE_CONTRACT,
	FLASK,
	FLASK_CONTRACT,
	GIVE_HOLYWATER,
	GIVE_RING,
	COMBAT_INFECT,
	COMBAT_CONTRACT,
	LIST,
	SET,
	SET_HELP,
	SET_VAMPIRE,
	SET_VAMPIRE_OTHER,
	SET_NOSFERATU,
	SET_NOSFERATU_OTHER,
	SET_INFECTION,
	SET_INFECTION_OTHER,
	SET_FOOD,
	SET_FOOD_OTHER,
	SET_HEALTH,
	SET_HEALTH_OTHER,
	VERSION,
	
	ALTAR_DARK,
	ALTAR_LIGHT,
	
	CONFIG,
	LANG,
	RELOAD
	
	// END OF LIST
	;

	public String getPermissionNode() {
		return "vampire." + this.name().toLowerCase(Locale.ROOT).replace("_", ".");
	}

	@Nullable
	@Contract(value = "null -> null", pure = true)
	public static Perm getPermFromString(String command) {
		Perm perm = null;

		if (command != null) {
			switch (command) {
				case "show":
					perm = Perm.SHOW;
					break;
				case "bloodlust":
					perm = Perm.MODE_BLOODLUST;
					break;
				case "intend":
					perm = Perm.MODE_INTENT;
					break;
				case "nightvision":
					perm = Perm.MODE_NIGHTVISION;
					break;
				case "batusi":
					perm = Perm.MODE_BATUSI;
					break;
				case "disguise batusi":
					perm = Perm.MODE_BATUSI_DISGUISE;
					break;
				case "offer":
					perm = Perm.TRADE_OFFER;
					break;
				case "accept":
					perm = Perm.TRADE_ACCEPT;
					break;
				case "flask":
					perm = Perm.FLASK;
					break;
				case "shriek":
					perm = Perm.SHRIEK;
					break;
				case "list":
					perm = Perm.LIST;
					break;
				case "set":
					perm = Perm.SET;
					break;
				case "version":
					perm = Perm.VERSION;
					break;
				case "vampire":
					perm = Perm.SET_VAMPIRE;
					break;
				case "nosferatu":
					perm = Perm.SET_NOSFERATU;
					break;
				case "infection":
					perm = Perm.SET_INFECTION;
					break;
				case "food":
					perm = Perm.SET_FOOD;
					break;
				case "health":
					perm = Perm.SET_HEALTH;
					break;
				case "help":
					perm = Perm.BASECOMMAND;
					break;
				default:
            }
		}

		return perm;
	}
}
