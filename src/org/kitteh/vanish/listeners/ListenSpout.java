package org.kitteh.vanish.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.kitteh.vanish.VanishPlugin;

public class ListenSpout implements Listener {

    private final VanishPlugin plugin;

    public ListenSpout(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        this.plugin.hooksSpoutAuth(event.getPlayer());
    }
}
