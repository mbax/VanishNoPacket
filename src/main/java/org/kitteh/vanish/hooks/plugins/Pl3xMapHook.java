package org.kitteh.vanish.hooks.plugins;

import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.Pl3xMapProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public class Pl3xMapHook extends Hook {
    private boolean enabled = false;
    private Pl3xMap pl3xMap;

    public Pl3xMapHook(@NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Pl3xMap");
        if (grab != null && grab.isEnabled()) {
            this.pl3xMap = Pl3xMapProvider.get();
            this.plugin.getLogger().info("Now hooking into Pl3xMap");
            this.enabled = true;
        } else {
            this.plugin.getLogger().info("You wanted Pl3xMap support. I could not find Pl3xMap.");
            this.pl3xMap = null;
            this.enabled = false;
        }
    }

    @Override
    public void onVanish(@NonNull Player player) {
        if (this.enabled && this.pl3xMap != null) {
            this.pl3xMap.playerManager().hidden(player.getUniqueId(), true);
        }
    }

    @Override
    public void onUnvanish(@NonNull Player player) {
        if (this.enabled && this.pl3xMap != null) {
            this.pl3xMap.playerManager().hidden(player.getUniqueId(), false);
        }
    }
}
