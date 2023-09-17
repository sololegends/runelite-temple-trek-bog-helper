package com.sololegends.runelite;

import net.runelite.client.config.*;

@ConfigGroup("Temple Trekking Bog Helper")
public interface TempleTrekkingBogHelperConfig extends Config {

	@ConfigItem(position = 0, section = "General", keyName = "cheat_mode", name = "Reveal Solutions", description = "When true show all firm spaces automatically")
	default boolean cheatMode() {
		return false;
	}

}
