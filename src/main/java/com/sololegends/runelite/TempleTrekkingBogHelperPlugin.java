package com.sololegends.runelite;

import com.google.inject.Inject;
import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(name = "Temple Trek Bog Helper", description = "Highlights firm bog ground as you find them", tags = {
		"temple", "trek", "trekking", "bog", "help", "solve" })
public class TempleTrekkingBogHelperPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private TempleTrekkingBogHelperOverlay bog_overlay;

	@Inject
	private OverlayManager overlay_manager;

	@Override
	protected void startUp() throws Exception {
		log.info("Starting Temple Trek Bog Helper");
		overlay_manager.add(bog_overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Stopping Temple Trek Bog Helper!");
		overlay_manager.remove(bog_overlay);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() != ChatMessageType.GAMEMESSAGE) {
			return;
		}
		if (event.getMessage().equals("This ground seems quite firm.")) {
			bog_overlay.setTargetFirm();
		}
		if (event.getMessage().equals("The ground seems firm enough.")) {
			bog_overlay.setFirm();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		// Check region
		if (client.getGameState() == GameState.LOGGED_IN) {
			Player player = client.getLocalPlayer();

			WorldPoint world = player.getWorldLocation();
			LocalPoint local = player.getLocalLocation();

			int region_id = world.getRegionID();
			if (client.isInInstancedRegion()) {
				region_id = WorldPoint.fromLocalInstance(client, local).getRegionID();
			}
			// If not in the bog, clear marked
			if (region_id != 8270) {
				bog_overlay.clearMarked();
			}
		}
	}

	@Provides
	TempleTrekkingBogHelperConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(TempleTrekkingBogHelperConfig.class);
	}
}
