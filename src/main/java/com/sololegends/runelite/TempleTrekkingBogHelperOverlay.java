package com.sololegends.runelite;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.sololegends.runelite.data.TileMarked;

import net.runelite.api.*;
import net.runelite.api.coords.*;
import net.runelite.client.ui.overlay.*;

public class TempleTrekkingBogHelperOverlay extends Overlay {

	private final Set<TileMarked> MARKED_TILES = new HashSet<>();

	private final Client client;
	private final TempleTrekkingBogHelperConfig config;

	@Inject
	private TempleTrekkingBogHelperOverlay(Client client, TempleTrekkingBogHelperPlugin plugin,
			TempleTrekkingBogHelperConfig config) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.config = config;
	}

	public void setTargetFirm() {
		Player player = client.getLocalPlayer();
		LocalPoint local = player.getLocalLocation();
		Direction angle = getNearestDirection(player.getOrientation());
		int sx = local.getSceneX();
		int sy = local.getSceneY();
		if (angle.equals(Direction.NORTH)) {
			sy++;
		} else if (angle.equals(Direction.SOUTH)) {
			sy--;
		} else if (angle.equals(Direction.EAST)) {
			sx++;
		} else if (angle.equals(Direction.WEST)) {
			sx--;
		}

		MARKED_TILES.add(new TileMarked(sx, sy, client.getPlane()));
	}

	public void setFirm() {
		Player player = client.getLocalPlayer();
		LocalPoint local = player.getLocalLocation();
		int sx = local.getSceneX();
		int sy = local.getSceneY();
		MARKED_TILES.add(new TileMarked(sx, sy, client.getPlane()));
	}

	public void clearMarked() {
		MARKED_TILES.clear();
	}

	public void markBogTiles(Graphics2D graphics) {
		Player player = client.getLocalPlayer();

		WorldPoint world = player.getWorldLocation();
		LocalPoint local = player.getLocalLocation();

		int region_id = world.getRegionID();
		if (client.isInInstancedRegion()) {
			region_id = WorldPoint.fromLocalInstance(client, local).getRegionID();
		}
		// If not in the bog, return
		if (region_id != 8270) {
			return;
		}
		// INt he bog here mark tiles
		int z = client.getPlane();
		Tile[][][] tiles = client.getScene().getTiles();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[z][x][y];
				// If a walkable bog tile
				TileObject tile_obj = tile.getGroundObject();
				if (tile_obj != null && tile_obj.getId() == 13838) {
					// Always draw in cheat mode
					if (config.cheatMode()) {
						OverlayUtil.renderTileOverlay(graphics, tile_obj, "FIRM", Color.GREEN);
						continue;
					}
					if (MARKED_TILES.contains(new TileMarked(x, y, z))) {
						OverlayUtil.renderTileOverlay(graphics, tile_obj, "FIRM", Color.GREEN);
					}
				}
			}
		}
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		markBogTiles(graphics);
		return null;
	}

	public Direction getNearestDirection(int angle) {
		int round = angle >>> 9;
		int up = angle & 256;
		if (up != 0) {
			// round up
			++round;
		}
		switch (round & 3) {
			case 0:
				return Direction.SOUTH;
			case 1:
				return Direction.WEST;
			case 2:
				return Direction.NORTH;
			case 3:
				return Direction.EAST;
			default:
				throw new IllegalStateException();
		}
	}
}
