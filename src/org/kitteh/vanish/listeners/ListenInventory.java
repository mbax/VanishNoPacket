package org.kitteh.vanish.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.kitteh.vanish.VanishPlugin;

public class ListenInventory implements Listener{

    private final VanishPlugin plugin;
    
    public ListenInventory(VanishPlugin instance) {
        this.plugin = instance;
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(plugin.haveInventoriesOpen.contains(event.getPlayer().getName())){
            plugin.haveInventoriesOpen.remove(event.getPlayer().getName());
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(plugin.haveInventoriesOpen.contains(event.getWhoClicked().getName())){
            event.setCancelled(true);
        }
    }
    
}
