package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;

public abstract class Hook {
    protected final VanishPlugin plugin;

    public Hook(@NonNull VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void onDisable() {

    }

    public void onEnable() {

    }

    public void onJoin(@NonNull Player player) {

    }

    public void onQuit(@NonNull Player player) {

    }

    public void onUnvanish(@NonNull Player player) {

    }

    public void onVanish(@NonNull Player player) {

    }
}