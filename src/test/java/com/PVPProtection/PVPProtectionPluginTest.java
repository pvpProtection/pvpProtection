package com.PVPProtection;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PVPProtectionPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PVPProtectionPlugin.class);
		RuneLite.main(args);
	}
}