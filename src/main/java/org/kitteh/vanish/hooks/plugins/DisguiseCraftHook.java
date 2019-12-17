package org.kitteh.vanish.hooks.plugins;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.hooks.Hook;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.api.PlayerDisguiseEvent;

public final class DisguiseCraftHook extends Hook implements Listener {
    private DisguiseCraftAPI disguiseCraft = null;

    public DisguiseCraftHook(VanishPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void beforeVanishChange(VanishStatusChangeEvent event) {
        if (this.disguiseCraft == null) {
            return;
        }
        final Player player = event.getPlayer();
        if (this.disguiseCraft.isDisguised(player)) {
            this.disguiseCraft.undisguisePlayer(player);
            player.sendMessage(ChatColor.AQUA + "You have been undisguised for toggling vanishing.");
        }
    }

    @EventHandler
    public void onDisguise(PlayerDisguiseEvent event) {
        if (this.disguiseCraft == null) {
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
            this.plugin.getLogger().info("Now hooking into DisguiseCraft");
            this.disguiseCraft = DisguiseCraft.getAPI();
            this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        }
    }
}