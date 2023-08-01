package com.PVPProtection;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import net.runelite.client.util.ImageUtil;

public class PVPProtectionUtil
{
	private static final BufferedImage SCAMMER_ICON = ImageUtil.resizeImage(ImageUtil.loadImageResource(PVPProtectionPlugin.class, "scammer.png"), 11, 11);
	private static final BufferedImage SMILEY = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "smiley.png");
	private static final BufferedImage RECRUIT = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "recruit.png");
	private static final BufferedImage CORPORAL = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "corporal.png");
	private static final BufferedImage SERGEANT = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "sergeant.png");
	private static final BufferedImage CAPTAIN = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "captain.png");
	private static final BufferedImage GENERAL = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "general.png");
	private static final BufferedImage LIEUTENANT = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "lieutenant.png");
	public static final HashMap<String, Color> COLORHM;

	static
	{
		COLORHM = new HashMap<>();
		COLORHM.put("DMer", new Color(234, 123, 91));
		COLORHM.put("Smiley", new Color(252, 242, 4));
		COLORHM.put("Recruit", new Color(252, 242, 4));
		COLORHM.put("Sergeant", new Color(252, 242, 4));
		COLORHM.put("Corporal", new Color(252, 242, 4));
		COLORHM.put("Scammer", Color.RED);
		COLORHM.put("Lieutenant", new Color(188, 84, 4));
		COLORHM.put("Captain", new Color(236, 236, 220));
		COLORHM.put("General", new Color(244, 204, 64));
	}

	public static BufferedImage getRankedIconSidePanel(String status)
	{
		switch (status)
		{
			case "DMer":
				return null;
			case "Smiley":
				return SMILEY;
			case "Recruit":
				return RECRUIT;
			case "Sergeant":
				return SERGEANT;
			case "Corporal":
				return CORPORAL;
			case "Lieutenant":
				return LIEUTENANT;
			case "Captain":
				return CAPTAIN;
			case "General":
				return GENERAL;
			case "Scammer":
				return SCAMMER_ICON;
			default:
				return null;
		}
	}

	public static BufferedImage getRankIconOverlay(String status)
	{
		if ("Scammer".equals(status))
		{
			return SCAMMER_ICON;
		}
		return null;
	}
}
