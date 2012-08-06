package org.kitteh.vanish.hooks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.api.PlayerDisguiseEvent;

public class DisguiseCraftHook extends Hook implements Listener {

    private DisguiseCraftAPI dcAPI = null;

    public DisguiseCraftHook(VanishPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void beforeVanishChange(VanishStatusChangeEvent event) {
        if (this.dcAPI == null) {
            return;
        }
        final Player player = event.getPlayer();
        if (this.dcAPI.isDisguised(player)) {
            this.dcAPI.undisguisePlayer(player);
            player.sendMessage(ChatColor.AQUA + "You have been undisguised for toggling vanishing.");
        }
    }

    @EventHandler
    public void onDisguise(PlayerDisguiseEvent event) {
        if (this.dcAPI == null) {
            return;
        }
        final Player player = event.getPlayer();
        if (this.plugin.getManager().isVanished(player.getName())) {
            this.plugin.getManager().toggleVanishQuiet(player, false);
            player.sendMessage(ChatColor.AQUA + "You have been unvanished for toggling disguising.");
        }
    }

    @Override
    public void onEnable() {
        final Plugin disguiseCraft = this.plugin.getServer().getPluginManager().getPlugin("DisguiseCraft");
        if (disguiseCraft != null) {
            this.plugin.log("Now hooking into DisguiseCraft");
            this.dcAPI = DisguiseCraft.getAPI();
            this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        }
    }

}
