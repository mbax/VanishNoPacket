package org.kitteh.vanish.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;

public final class ListenInventory implements Listener {
    private final VanishPlugin plugin;

    public ListenInventory(@NonNull VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NonNull InventoryClickEvent event) {
        if (this.plugin.chestFakeInUse(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(@NonNull InventoryCloseEvent event) {
        if (this.plugin.chestFakeInUse(event.getPlayer().getName())) {
            this.plugin.chestFakeClose(event.getPlayer().getName());
        }
    }
}