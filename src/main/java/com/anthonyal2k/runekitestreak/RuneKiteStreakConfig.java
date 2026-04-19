package com.anthonyal2k.runekitestreak;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("runekitestreak")
public interface RuneKiteStreakConfig extends Config {
	@ConfigItem(keyName = "sourceName", name = "Loot source", position = 0,
			description = "Name of the loot source to watch (as shown in Loot Tracker)")
	default String sourceName() { return "Wilderness Agility Dispenser"; }

	@ConfigItem(keyName = "announceInChat", name = "Announce in chat", position = 1,
			description = "Post streak updates to the game chat")
	default boolean announceInChat() { return true; }
}