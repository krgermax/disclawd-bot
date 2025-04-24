package org.clawd.data.inventory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryCache {
    private final List<Inventory> inventories;
    private final Lock lock = new ReentrantLock();

    public InventoryCache() {
        this.inventories = new ArrayList<>();
    }

    public void cleanUpCache() {
        lock.lock();
        try {
            if (inventories.isEmpty())
                return;

            int previousSize = this.inventories.size();
            LocalDateTime now = LocalDateTime.now();
            inventories.removeIf(
                    inventory -> inventory.getTimestamp().isBefore(now.minusMinutes(Constants.CACHE_EXPIRY_MINUTES))
            );
            Main.LOGGER.info("Inventory cache has been cleaned up, " + previousSize + "->" + inventories.size());
        } finally {
            lock.unlock();
        }
    }

    public Inventory addInventory(SlashCommandInteractionEvent event) {
        lock.lock();
        try {
            String userID = event.getUser().getId();
            Optional<Inventory> inventoryOpt = getInventory(userID);
            if (inventoryOpt.isPresent()) {
                Inventory existingInventory = inventoryOpt.get();
                existingInventory.setTimestamp(LocalDateTime.now());
                return existingInventory;
            } else {
                Inventory newInventory = new Inventory(userID, event);
                this.inventories.add(newInventory);
                return newInventory;
            }
        } finally {
            lock.unlock();
        }
    }

    public Inventory addInventory(ButtonInteractionEvent event) {
        lock.lock();
        try {
            String userID = event.getUser().getId();
            Optional<Inventory> inventoryOpt = getInventory(userID);
            if (inventoryOpt.isPresent()) {
                Inventory existingInventory = inventoryOpt.get();
                existingInventory.setTimestamp(LocalDateTime.now());
                return existingInventory;
            } else {
                Inventory newInventory = new Inventory(userID, event);
                this.inventories.add(newInventory);
                return newInventory;
            }
        } finally {
            lock.unlock();
        }
    }

    public Inventory forceInventoryUpdate(ButtonInteractionEvent event) {
        lock.lock();
        try {
            Optional<Inventory> inventoryOpt = getInventory(event.getUser().getId());
            Inventory inventory = inventoryOpt.orElseGet(() -> addInventory(event));
            inventory.createPagesWrapper(event);
            inventory.setTimestamp(LocalDateTime.now());
            return inventory;
        } finally {
            lock.unlock();
        }
    }

    private Optional<Inventory> getInventory(String userID) {
        return this.inventories.stream()
                .filter(inventory -> inventory.getUserID().equals(userID))
                .findFirst();
    }
}
