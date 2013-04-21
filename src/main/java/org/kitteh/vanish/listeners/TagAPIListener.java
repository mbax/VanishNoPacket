package org.kitteh.vanish.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.vanish.VanishPlugin;

public final class TagAPIListener implements Listener {
    private final VanishPlugin plugin;

    public TagAPIListener(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNameTag(PlayerReceiveNameTagEvent event) {
        if (this.plugin.getManager().isVanished(event.getNamedPlayer())) {
            event.setTag(ChatColor.AQUA + event.getNamedPlayer().getName());
        }
    }
}