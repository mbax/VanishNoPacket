package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

public abstract class Hook {
    protected final VanishPlugin plugin;

    public Hook(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void onDisable() {

    }

    public void onEnable() {

    }

    public void onJoin(Player player) {

    }

    public void onQuit(Player player) {

    }

    public void onUnvanish(Player player) {

    }

    public void onVanish(Player player) {

    }
}