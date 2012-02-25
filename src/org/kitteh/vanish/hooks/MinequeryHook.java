package org.kitteh.vanish.hooks;

import net.minestatus.minequery.Minequery;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

public class MinequeryHook {
	private final VanishPlugin plugin;

    private Minequery minequery;
    private boolean enabled;

    public MinequeryHook(VanishPlugin plugin) {
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
            this.grabMinequery();
        } else {
            this.minequery = null;
        }
    }

    public void unvanish(Player player) {
        if (this.enabled && (this.minequery != null) && !player.hasPermission("vanish.hooks.minequery.alwayshidden")) {
            this.minequery.setPlayerVisiblity(player, false);
        }
    }

    public void vanish(Player player) {
        if (this.enabled && (this.minequery != null)) {
            this.minequery.setPlayerVisiblity(player, true);
        }
    }

    private void grabMinequery() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Minequery");
        if (grab != null) {
        	double version = Double.parseDouble(grab.getDescription().getVersion());
        	if(version < 1.6)
        	{
        		this.plugin.log("The running version of Minequery doesn't support VanishNoPacket. Please update to 1.6 or later.");
                this.minequery = null;
                this.enabled = false;
                return;
        	}
            this.minequery = ((Minequery) grab);
            this.plugin.log("Now hooking into Minequery");
        } else {
            this.plugin.log("You wanted Minequery support. I could not find Minequery.");
            this.minequery = null;
            this.enabled = false;
        }
    }
}
