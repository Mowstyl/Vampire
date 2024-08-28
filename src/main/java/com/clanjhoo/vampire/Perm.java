package com.clanjhoo.vampire;

import com.clanjhoo.vampire.util.ResourceUtil;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Perm {
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	BASECOMMAND,
	
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
	FLASK,
	COMBAT_INFECT,
	COMBAT_CONTRACT,
	LIST,
	SET,
	SET_VAMPIRE_TRUE,
	SET_VAMPIRE_FALSE,
	SET_NOSFERATU_TRUE,
	SET_NOSFERATU_FALSE,
	SET_INFECTION,
	SET_FOOD,
	SET_HEALTH,
	VERSION,
	
	ALTAR_DARK,
	ALTAR_LIGHT,
	
	CONFIG,
	LANG,
	
	// END OF LIST
	;

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
				case "vampire on":
					perm = Perm.SET_VAMPIRE_TRUE;
					break;
				case "vampire off":
					perm = Perm.SET_VAMPIRE_FALSE;
					break;
				case "nosferatu on":
					perm = Perm.SET_NOSFERATU_TRUE;
					break;
				case "nosferatu off":
					perm = Perm.SET_NOSFERATU_FALSE;
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
