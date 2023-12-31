/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.PVPProtection.ui;

import com.PVPProtection.PVPProtectionConfig;
import com.PVPProtection.PVPProtectionPlugin;
import com.PVPProtection.PVPProtectionUtil;
import com.PVPProtection.data.PartyPlayer;
import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Constants;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

public class PlayerBanner extends JPanel
{
	private static final Dimension STAT_ICON_SIZE = new Dimension(18, 18);
	private static final Dimension ICON_SIZE = new Dimension(Constants.ITEM_SPRITE_WIDTH - 6, Constants.ITEM_SPRITE_HEIGHT - 4);
	private static final BufferedImage COLLAPSED_ICON = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "collapsed.png");
	private static final BufferedImage EXPANDED_ICON = ImageUtil.loadImageResource(PVPProtectionPlugin.class, "expanded.png");
	@Getter
	private final JPanel statsPanel = new JPanel();
	@Getter
	private final JPanel infoPanel = new JPanel();
	private final Map<String, JLabel> statLabels = new HashMap<>();
	private final Map<String, JLabel> iconLabels = new HashMap<>();
	@Getter
	private final JLabel expandIcon = new JLabel();
	@Getter
	private final ImageIcon expandIconUp;
	@Getter
	private final ImageIcon expandIconDown;

	@Getter
	private final JCheckBox trustedPlayerButton = new JCheckBox();

	@Setter
	@Getter
	private PartyPlayer player;

	private boolean checkIcon;
	private final JLabel rankIcon = new JLabel();
	private final JLabel worldLabel = new JLabel();
	private final JLabel iconLabel = new JLabel();

	private final PVPProtectionConfig config;
	private final PVPProtectionPlugin plugin;

	public PlayerBanner(final PartyPlayer player, boolean expanded, SpriteManager spriteManager, PVPProtectionConfig config, PVPProtectionPlugin plugin)
	{
		super();
		this.config = config;
		this.player = player;
		this.plugin = plugin;
		this.setLayout(new GridBagLayout());
		this.setBorder(new EmptyBorder(5, 5, 0, 5));

		statsPanel.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 14, 25));
		statsPanel.setLayout(new GridLayout(0, 3));
		statsPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		statsPanel.setOpaque(true);

		infoPanel.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 14, 25));
		infoPanel.setLayout(new GridLayout(0, 2));
		infoPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		infoPanel.setOpaque(true);

		expandIconDown = new ImageIcon(EXPANDED_ICON);
		expandIconUp = new ImageIcon(COLLAPSED_ICON);
		if (expanded)
		{
			expandIcon.setIcon(expandIconUp);
		}
		else
		{
			expandIcon.setIcon(expandIconDown);
		}

		statsPanel.add(createIconPanel(spriteManager, SpriteID.SPELL_VENGEANCE_OTHER, "IsVenged", ""));
		statsPanel.add(createIconPanel(spriteManager, SpriteID.SKILL_HITPOINTS, Skill.HITPOINTS.getName(), String.valueOf(player.getSkillBoostedLevel(Skill.HITPOINTS))));
		statsPanel.add(createIconPanel(spriteManager, SpriteID.PLAYER_KILLER_SKULL, "Tier", player.getTier()));

		final JLabel trustLabel = new JLabel("Trust this player:");
		trustLabel.setToolTipText("If selected the inventory will show the players GP and/or Platinum tokens.");

		infoPanel.add(trustLabel);
		trustedPlayerButton.setSelected(config.trustAllPlayers());
		infoPanel.add(trustedPlayerButton);

		recreatePanel();
	}

	public void recreatePanel()
	{
		removeAll();

		final GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1.0;
		c.ipady = 4;

		// Add avatar label regardless of if one exists just to have UI matching
		iconLabel.setBorder(new MatteBorder(1, 1, 1, 1, ColorScheme.DARKER_GRAY_HOVER_COLOR));
		iconLabel.setPreferredSize(ICON_SIZE);
		iconLabel.setMinimumSize(ICON_SIZE);
		iconLabel.setOpaque(false);

		checkIcon = player.getMember().getAvatar() == null;
		if (!checkIcon)
		{
			addIcon();
		}

		add(iconLabel, c);
		c.gridx++;

		final JPanel nameContainer = new JPanel(new GridLayout(2, 1));
		nameContainer.setBorder(new EmptyBorder(0, 5, 0, 0));
		nameContainer.setOpaque(false);

		final JLabel usernameLabel = new JLabel();
		usernameLabel.setLayout(new OverlayLayout(usernameLabel));
		usernameLabel.setHorizontalTextPosition(JLabel.LEFT);
		if (Strings.isNullOrEmpty(player.getUsername()))
		{
			usernameLabel.setText("Not logged in");
		}
		else
		{
			final String levelText = player.getStats() == null ? "" : " (level-" + player.getStats().getCombatLevel() + ")";
			usernameLabel.setText(player.getUsername() + levelText);
			String rsn = player.getUsername();
			String hwid = player.getHWID();
			String accid = player.getUserUnique();
			String status = getStatusFromInfo(rsn, hwid, accid);
			player.setTier(status);
			if (config.recolorRSNonBanner())
			{
				if (!player.getTier().equals("DMer"))
				{
					usernameLabel.setForeground(PVPProtectionUtil.COLORHM.get(player.getTier()));
				}
			}
		}

		expandIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);
		usernameLabel.add(expandIcon, BorderLayout.EAST);
		nameContainer.add(usernameLabel);

		worldLabel.setLayout(new OverlayLayout(worldLabel));
		worldLabel.setHorizontalTextPosition(JLabel.RIGHT);
		worldLabel.setText("Not logged in");

		rankIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);

		if (Strings.isNullOrEmpty(player.getUsername()))
		{
			worldLabel.setText("");
		}
		updateWorld(player.getWorld());
		nameContainer.add(worldLabel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(nameContainer, c);

		refreshStats();
		c.gridy++;
		c.weightx = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		add(statsPanel, c);

		c.gridy++;
		c.weightx = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		add(infoPanel, c);
		revalidate();
		repaint();
	}

	private void addIcon()
	{
		final BufferedImage resized = ImageUtil.resizeImage(player.getMember().getAvatar(), Constants.ITEM_SPRITE_WIDTH - 8, Constants.ITEM_SPRITE_HEIGHT - 4);
		iconLabel.setIcon(new ImageIcon(resized));
	}

	public void refreshStats()
	{
		if (checkIcon)
		{
			if (player.getMember().getAvatar() != null)
			{
				addIcon();
				checkIcon = false;
			}
		}

		statLabels.getOrDefault("Tier", new JLabel()).setText(player.getTier());
		statLabels.getOrDefault(Skill.HITPOINTS.getName(), new JLabel()).setText(String.valueOf(player.getSkillBoostedLevel(Skill.HITPOINTS)));

		iconLabels.getOrDefault("pchash", new JLabel()).setText("HWID: " + player.getHWID());
		iconLabels.getOrDefault("acchash", new JLabel()).setText("RID: " + player.getUserUnique());

		statsPanel.revalidate();
		statsPanel.repaint();
		infoPanel.revalidate();
		infoPanel.repaint();
	}

	private JPanel createIconPanel(final SpriteManager spriteManager, final int spriteID, final String name, final String value)
	{
		final JLabel iconLabel = new JLabel();
		iconLabel.setPreferredSize(STAT_ICON_SIZE);
		iconLabels.put(name, iconLabel);
		setSpriteIcon(name, spriteID, spriteManager);

		final JLabel textLabel = new JLabel(value);
		textLabel.setHorizontalAlignment(JLabel.LEFT);
		textLabel.setHorizontalTextPosition(JLabel.LEFT);
		statLabels.put(name, textLabel);

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(iconLabel, BorderLayout.WEST);
		panel.add(textLabel, BorderLayout.CENTER);
		panel.setOpaque(false);
		panel.setToolTipText(name);

		return panel;
	}

	private JPanel createTextPanel(final String name, final String value)
	{
		final JLabel textLabel = new JLabel(value);
		textLabel.setHorizontalAlignment(JLabel.LEFT);
		textLabel.setHorizontalTextPosition(JLabel.LEFT);
		statLabels.put(name, textLabel);

		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(textLabel, BorderLayout.WEST);
		panel.setOpaque(false);

		return panel;
	}

	private void setSpriteIcon(String statLabelKey, final int spriteID, final SpriteManager spriteManager)
	{
		final JLabel label = iconLabels.get(statLabelKey);
		spriteManager.getSpriteAsync(spriteID, 0, img ->
			SwingUtilities.invokeLater(() ->
			{
				if (spriteID == SpriteID.SKILL_PRAYER)
				{
					label.setIcon(new ImageIcon(ImageUtil.resizeImage(img, STAT_ICON_SIZE.width + 2, STAT_ICON_SIZE.height + 2)));
				}
				else
				{
					label.setIcon(new ImageIcon(ImageUtil.resizeImage(img, STAT_ICON_SIZE.width, STAT_ICON_SIZE.height)));
				}
				label.revalidate();
				label.repaint();
			}));
	}

	private void setBufferedIcon(String statLabelKey, final BufferedImage img)
	{
		final JLabel label = iconLabels.get(statLabelKey);
		if (player.getTier().equals("Scammer"))
		{
			SwingUtilities.invokeLater(() ->
			{
				ImageIcon ic = new ImageIcon(ImageUtil.resizeImage(img, STAT_ICON_SIZE.width, STAT_ICON_SIZE.height));
				label.setIcon(ic);
				label.revalidate();
				label.repaint();
			});
		}
		else
		{
			SwingUtilities.invokeLater(() ->
			{
				ImageIcon ic = new ImageIcon(img);
				label.setIcon(ic);
				label.revalidate();
				label.repaint();
			});
		}
	}

	public void setVenged(boolean currentVenged, SpriteManager spriteManager)
	{
		// If the new value is the same then do nothing
		if (!currentVenged)
		{
			setSpriteIcon("IsVenged", SpriteID.SPELL_VENGEANCE_OTHER, spriteManager);
		}
		else
		{
			setSpriteIcon("IsVenged", SpriteID.SPELL_VENGEANCE, spriteManager);
		}
		statsPanel.revalidate();
		statsPanel.repaint();
	}

	public void setStreamerIcon(String rank, SpriteManager spriteManager)
	{
		BufferedImage img = PVPProtectionUtil.getRankedIconSidePanel(rank);
		if (img != null)
		{
			setBufferedIcon("Tier", img);
		}
		else
		{
			setSpriteIcon("Tier", SpriteID.PLAYER_KILLER_SKULL, spriteManager);
		}
		statsPanel.revalidate();
		statsPanel.repaint();
	}

	public void updateWorld(int world)
	{
		if (world == -1)
		{
			worldLabel.setText("Hidden");
		}
		else if (world == 0)
		{
			worldLabel.setText("Logged out");
		}
		else
		{
			worldLabel.setText("World " + world);
		}
	}

	public String getStatusFromInfo(String rsn, String hwid, String hash)
	{
		String niceRSN = Text.toJagexName(rsn).toLowerCase();
		if (plugin.getRankedMappings().containsKey(niceRSN))
		{
			return plugin.getRankedMappings().get(niceRSN);
		}

		if (plugin.isScammerRSN(niceRSN) || plugin.isScammerHWID(hwid) || plugin.isScammerHash(hash))
		{
			return "Scammer";
		}

		return "DMer";
	}
}