package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.Messages;
import org.kitteh.vanish.VanishPlugin;

import com.earth2me.essentials.IEssentials;

@SuppressWarnings("deprecation")
public class EssentialsHook {
    private final VanishPlugin plugin;

    private IEssentials essentials;
    private boolean enabled;

    public EssentialsHook(VanishPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }

    public void onPluginDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.unvanish(player);
            }
        }
    }

    public void onPluginEnable(boolean enableEssentials) {
        this.enabled = enableEssentials;
        if (enableEssentials) {
            this.grabEssentials();
        } else {
            this.essentials = null;
        }
    }

    public void unvanish(Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, false);
        }
    }

    public void vanish(Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, true);
        }
    }

    private void grabEssentials() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (grab != null) {
            this.essentials = ((IEssentials) grab);
            this.plugin.log(Messages.getString("EssentialsHook.HookingIntoEssentials"));
        } else {
            this.plugin.log(Messages.getString("EssentialsHook.CantFindEssentials"));
            this.essentials = null;
            this.enabled = false;
        }
    }

    private void setHidden(Player player, boolean hide) {
        if (this.enabled && (this.essentials != null)) {
            this.essentials.getUser(player).setHidden(hide);
        }
    }
}
