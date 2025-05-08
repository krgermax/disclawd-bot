package com.github.krgermax.data.inventory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.main.Main;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryCache {

    private final Map<String, Inventory> inventories = new ConcurrentHashMap<>();

    private static final long INV_CACHE_EXPIRY_MINUTES = 5;
    public static final int INV_CACHE_PERIOD_MINUTES = 2;

    public void cleanUpCache() {
        int before = inventories.size();
        LocalDateTime now = LocalDateTime.now();

        inventories.entrySet().removeIf(entry ->
                entry.getValue().getTimestamp().isBefore(now.minusMinutes(INV_CACHE_EXPIRY_MINUTES)));

        Main.LOGGER.info("Inventory cache cleaned: " + before + " -> " + inventories.size());
    }

    public Inventory addInventory(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        return inventories.compute(userID, (id, existing) -> {
            if (existing != null) {
                existing.setTimestamp(LocalDateTime.now());
                return existing;
            }
            return new Inventory(userID, event);
        });
    }

    public Inventory addInventory(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        return inventories.compute(userID, (id, existing) -> {
            if (existing != null) {
                existing.setTimestamp(LocalDateTime.now());
                return existing;
            }
            return new Inventory(userID, event);
        });
    }

    public Inventory forceInventoryUpdate(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        Inventory inv = inventories.compute(userID, (id, existing) -> {
            if (existing == null) return new Inventory(userID, event);
            return existing;
        });

        inv.createPagesWrapper(event);
        inv.setTimestamp(LocalDateTime.now());
        return inv;
    }
}