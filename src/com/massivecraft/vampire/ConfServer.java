package com.massivecraft.vampire;

import java.util.List;

import com.massivecraft.mcore4.SimpleConfig;
import com.massivecraft.mcore4.util.MUtil;

public class ConfServer extends SimpleConfig
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static String dburi = "default";
	public final static transient String playerContext = "vampire_player";
	public final static transient String configContext = "vampire_config";
	
	public static int taskInterval = 10; // Defines how often the task runs.
	
	public static List<String> baseCommandAliases = MUtil.list("v");
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	public static transient ConfServer i = new ConfServer();
	private ConfServer()
	{
		super(P.p);
	}
}
