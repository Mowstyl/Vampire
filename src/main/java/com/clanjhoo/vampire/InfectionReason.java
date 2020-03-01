package com.clanjhoo.vampire;

import com.clanjhoo.vampire.entity.UPlayer;
import com.clanjhoo.vampire.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public enum InfectionReason
{
	ALTAR(true, false, "altar", "<i>You infected yourself using an <h>altar<i>.", "<i>%1$s was infected using an <h>altar<i>."),
	COMBAT_MISTAKE(false, true, "combat mistake", "<h>%2$s <i>infected you during combat by mistake.", "<h>%2$s <i>infected %1$s during combat by mistake."),
	COMBAT_INTENDED(false, true, "combat intended", "<h>%2$s <i>infected you during combat on purpose.", "<h>%2$s <i>infected %1$s during combat on purpose."),
	TRADE(false, true, "offer", "<i>You were infected from drinking <h>%2$s<i>'s blood.", "<i>%1$s was infected from drinking <h>%2$s<i>'s blood."),
	FLASK(true, false, "blood flask", "<i>You were infected by a <h>blood flask<i>.", "<i>%1$s was infected by a <h>blood flask<i>."),
	OPERATOR(true, false, "evil powers", "<i>You were infected by <h>evil powers<i>.", "<i>%1$s was infected by <h>evil powers<i>."),
	UNKNOWN(true, false, "unknown", "<i>You were infected for <h>unknown <i>reasons.", "<i>%1$s was infected for <h>unknown <i>reasons."),
	;
	
	// Would the victim notice this way of infection?
	private final boolean noticeable;
	public boolean isNoticeable() { return this.noticeable; }
		
	// Was another player the reason?
	private final boolean maker;
	public boolean isMaker() { return this.maker; }
	
	// Short name for the reason.
	private final String shortname;
	public String getShortname() { return this.shortname; }
	
	// Desc when showing yourself.
	private final String selfdesc;
	public String getSelfdesc() { return this.selfdesc; }
	
	// Desc when showing other player.
	private final String otherdesc;
	public String getOtherdesc() { return this.otherdesc; }
	
	InfectionReason(final boolean notice, final boolean player, final String shortname, final String selfdesc, final String otherdesc)
	{
		this.noticeable = notice;
		this.maker = player;
		this.shortname = shortname;
		this.selfdesc = selfdesc;
		this.otherdesc = otherdesc;
	}
	
	public String getDesc(UPlayer uplayer, boolean self)
	{
		String description;

		switch (this) {
			case COMBAT_MISTAKE:
			case COMBAT_INTENDED:
			case TRADE:
				OfflinePlayer maybeMaker = Bukkit.getOfflinePlayer(uplayer.getMakerUUID());
				String makerName = maybeMaker != null ? maybeMaker.getName() : uplayer.getMakerUUID().toString();
				if (self)
					description = TextUtil.parse(this.selfdesc, "", makerName);
				else
					description = TextUtil.parse(this.otherdesc, uplayer.getPlayer().getDisplayName(), makerName);
				break;
			default:
				if (self)
					description = TextUtil.parse(this.selfdesc);
				else
					description = TextUtil.parse(this.otherdesc, uplayer.getPlayer().getDisplayName());
		}

		return description;
	}

	public static InfectionReason fromName(String name) {
		InfectionReason reason = InfectionReason.UNKNOWN;

		if (name.equalsIgnoreCase("ALTAR"))
			reason = InfectionReason.ALTAR;
		else if (name.equalsIgnoreCase("COMBAT_MISTAKE"))
			reason = InfectionReason.COMBAT_MISTAKE;
		else if (name.equalsIgnoreCase("COMBAT_INTENDED"))
			reason = InfectionReason.COMBAT_INTENDED;
		else if (name.equalsIgnoreCase("TRADE"))
			reason = InfectionReason.TRADE;
		else if (name.equalsIgnoreCase("FLASK"))
			reason = InfectionReason.FLASK;
		else if (name.equalsIgnoreCase("OPERATOR"))
			reason = InfectionReason.OPERATOR;

		return reason;
	}
}
