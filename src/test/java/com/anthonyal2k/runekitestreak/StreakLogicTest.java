package com.anthonyal2k.runekitestreak;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.http.api.loottracker.LootRecordType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

// 1. Tell JUnit to use Mockito
@RunWith(MockitoJUnitRunner.class)
public class StreakLogicTest {

    // 2. Create a mock version of your config
    @Mock
    private RuneKiteStreakConfig config;

    // 3. Inject the mock directly into the plugin, replacing the "new" keyword
    @InjectMocks
    private RuneKiteStreakPlugin plugin;

    @Before
    public void setup() {
        // 4. Define exactly what the mock config should return when the plugin asks for it
        when(config.sourceName()).thenReturn("Wilderness Agility Dispenser");

        // We set this to false so the plugin doesn't try to use the missing ChatMessageManager
        when(config.announceInChat()).thenReturn(false);
    }

    private LootReceived lap(int... itemIds) {
        List<ItemStack> items = Arrays.stream(itemIds)
                .mapToObj(id -> new ItemStack(id, 1))
                .collect(java.util.stream.Collectors.toList());
        return new LootReceived("Wilderness Agility Dispenser", 0, LootRecordType.EVENT, items, 1, null);
    }

    @Test
    public void kiteDropsIncrementStreak() {
        plugin.onLootReceived(lap(ItemID.RUNE_KITESHIELD, ItemID.COAL));
        plugin.onLootReceived(lap(ItemID.RUNE_KITESHIELD));
        plugin.onLootReceived(lap(ItemID.RUNE_KITESHIELD, ItemID.MAGIC_LOGS));
        assertEquals(3, plugin.currentStreak);
        assertEquals(3, plugin.longestStreak);
    }

    @Test
    public void nonKiteLapBreaksStreak() {
        plugin.onLootReceived(lap(ItemID.RUNE_KITESHIELD));
        plugin.onLootReceived(lap(ItemID.RUNE_KITESHIELD));
        plugin.onLootReceived(lap(ItemID.COAL, ItemID.MAGIC_LOGS)); // no kite
        assertEquals(0, plugin.currentStreak);
        assertEquals(2, plugin.longestStreak); // best preserved
    }

    @Test
    public void wrongSourceIgnored() {
        LootReceived fromMonster = new LootReceived(
                "Some Other Source", 0, LootRecordType.EVENT,
                Collections.singletonList(new ItemStack(ItemID.RUNE_KITESHIELD, 1)), 1, null);
        plugin.onLootReceived(fromMonster);
        assertEquals(0, plugin.currentStreak);
    }
}