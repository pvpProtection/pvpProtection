package com.PVPProtection;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class PlayerMemberTileTierOverlay extends Overlay
{
	private final PartyMemberIndicatorService playerIndicatorsService;
	private final PVPProtectionConfig config;

	@Inject
	private PlayerMemberTileTierOverlay(PVPProtectionConfig config, PartyMemberIndicatorService playerIndicatorsService)
	{
		this.config = config;
		this.playerIndicatorsService = playerIndicatorsService;
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.drawTiles())
		{
			return null;
		}

		playerIndicatorsService.forEachPlayer((player, decorations) ->
		{
			final Polygon poly = player.getCanvasTilePoly();

			if (poly != null)
			{
				OverlayUtil.renderPolygon(graphics, poly, decorations.getColor());
			}
		});

		return null;
	}
}