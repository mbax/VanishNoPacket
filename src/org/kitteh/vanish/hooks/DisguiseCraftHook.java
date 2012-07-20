package org.kitteh.vanish.hooks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.api.PlayerDisguiseEvent;

public class DisguiseCraftHook extends Hook implements Listener {
    
    private DisguiseCraftAPI dcAPI = null;

    public DisguiseCraftHook(VanishPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public void onEnable() {
        Plugin disguiseCraft = plugin.getServer().getPluginManager().getPlugin("DisguiseCraft");
        if (disguiseCraft == null) {
            plugin.log("You wanted DisguiseCraft support. I could not find DisguiseCraft.");
        } else {
            plugin.log("Now hooking into DisguiseCraft");
            dcAPI = DisguiseCraft.getAPI();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }
    
    @EventHandler
    public void beforeVanishChange(VanishStatusChangeEvent event) {
        if(dcAPI == null){
            return;
        }
        Player player = event.getPlayer();
        if (dcAPI.isDisguised(player)) {
            dcAPI.undisguisePlayer(player);
            player.sendMessage(ChatColor.AQUA + "You have been undisguised for vanishing.");
        }
    }
    
    @EventHandler
    public void onDisguise(PlayerDisguiseEvent event) {
        if(dcAPI == null){
            return;
        }
        Player player = event.getPlayer();
        try {
            if (VanishNoPacket.isVanished(player.getName())) {
                VanishNoPacket.toggleVanishSilent(player);
                player.sendMessage(ChatColor.AQUA + "You have been unvanished for disguising.");
            }
        } catch (VanishNotLoadedException e) {
        }
    }

}
