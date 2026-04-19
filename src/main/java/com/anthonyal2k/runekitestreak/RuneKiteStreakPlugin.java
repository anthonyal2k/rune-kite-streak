package com.anthonyal2k.runekitestreak;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;

@Slf4j // Added for standard RuneLite logging
@PluginDescriptor(
		name = "Rune Kite Streak",
		description = "Counts consecutive Wildy Agility laps that drop a rune kiteshield",
		tags = {"agility", "wilderness", "loot", "streak"}
)
public class RuneKiteStreakPlugin extends Plugin {

	private static final int TARGET_ITEM_ID = ItemID.RUNE_KITESHIELD;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private RuneKiteStreakConfig config;

	int currentStreak = 0;
	int longestStreak = 0;

	@Provides
	RuneKiteStreakConfig provideConfig(ConfigManager cm) {
		return cm.getConfig(RuneKiteStreakConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		log.info("Rune Kite Streak started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Rune Kite Streak stopped!");
		// REQUIRED: Reset your state variables so the plugin shuts down cleanly
		currentStreak = 0;
		longestStreak = 0;
	}

	@Subscribe
	public void onLootReceived(LootReceived event) {
		// Removed null check: Guice guarantees 'config' is injected
		String expectedSource = config.sourceName();
		if (!event.getName().equalsIgnoreCase(expectedSource)) return;

		boolean gotKite = event.getItems().stream()
				.anyMatch(i -> i.getId() == TARGET_ITEM_ID);

		if (gotKite) {
			currentStreak++;
			if (currentStreak > longestStreak) longestStreak = currentStreak;
			announce("Rune kite streak: " + currentStreak + "  (best: " + longestStreak + ")");
		} else {
			if (currentStreak > 0) {
				announce("Streak broken at " + currentStreak + ".  Best still: " + longestStreak);
			}
			currentStreak = 0;
		}
	}

	private void announce(String text) {
		// Removed null checks: Guice guarantees these are injected
		if (!config.announceInChat()) return;

		String msg = new ChatMessageBuilder().append(text).build();
		chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(msg)
				.build());
	}
}