package com.clanjhoo.vampire;

import com.clanjhoo.vampire.keyproviders.InfectionMessageKeys;

public enum InfectionReason
{
	ALTAR(true, false, "altar", InfectionMessageKeys.ALTAR),
	COMBAT_MISTAKE(false, true, "combat mistake", InfectionMessageKeys.COMBAT_MISTAKE),
	COMBAT_INTENDED(false, true, "combat intended", InfectionMessageKeys.COMBAT_INTENDED),
	TRADE(false, true, "offer", InfectionMessageKeys.TRADE),
	FLASK(true, false, "blood flask", InfectionMessageKeys.FLASK),
	OPERATOR(true, false, "evil powers", InfectionMessageKeys.COMMAND),
	UNKNOWN(true, false, "unknown", InfectionMessageKeys.UNKNOWN);
	
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
	private final InfectionMessageKeys descKey;
	public InfectionMessageKeys getDescKey() { return this.descKey; }
	
	InfectionReason(final boolean notice, final boolean player, final String shortname, final InfectionMessageKeys descKey)
	{
		this.noticeable = notice;
		this.maker = player;
		this.shortname = shortname;
		this.descKey = descKey;
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
