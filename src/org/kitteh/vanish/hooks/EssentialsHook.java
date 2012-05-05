package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import com.earth2me.essentials.IEssentials;

@SuppressWarnings("deprecation")
public class EssentialsHook extends Hook {
    private final VanishPlugin plugin;

    private IEssentials essentials;
    private boolean enabled = false;

    public EssentialsHook(VanishPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.onUnvanish(player);
            }
        }
        this.essentials = null;
    }

    @Override
    public void onEnable() {
        this.enabled = true;
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (grab != null) {
            this.essentials = ((IEssentials) grab);
            this.plugin.log("Now hooking into Essentials");
        } else {
            this.plugin.log("You wanted Essentials support. I could not find Essentials.");
            this.essentials = null;
            this.enabled = false;
        }
    }

    @Override
    public void onUnvanish(Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, false);
        }
    }

    @Override
    public void onVanish(Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, true);
        }
    }

    private void setHidden(Player player, boolean hide) {
        if (this.enabled && (this.essentials != null)) {
            this.essentials.getUser(player).setHidden(hide);
        }
    }
}
