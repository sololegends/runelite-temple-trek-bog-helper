package com.sololegends.runelite;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TempleTrekkingBogHelperPluginTest {
	public static void main(String[] args) throws Exception {
		ExternalPluginManager.loadBuiltin(TempleTrekkingBogHelperPlugin.class);
		RuneLite.main(args);
	}
}