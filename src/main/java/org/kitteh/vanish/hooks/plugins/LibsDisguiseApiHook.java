package org.kitteh.vanish.hooks.plugins;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.events.DisguiseEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.kitteh.vanish.hooks.Hook;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 15/02/2017.
 */
public class LibsDisguiseApiHook extends Hook implements Listener{
private boolean enabled = false;


    public LibsDisguiseApiHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin LibDisguise = this.plugin.getServer().getPluginManager().getPlugin("Libsdisguises");
        if (LibDisguise != null) {
            this.plugin.getLogger().info("Now hooking into LibsDisguises");
            this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
            this.enabled = true;

        }
    }

    @EventHandler
    public void onDisguise(DisguiseEvent event) {
        if (!this.enabled) {
            return;
        }
        if(event.getEntity() instanceof Player){
        final Player player = (Player) event.getEntity();
        if (this.plugin.getManager().isVanished(player.getName())) {
            this.plugin.getManager().toggleVanishQuiet(player, false);
            player.sendMessage(ChatColor.AQUA + "You have been unvanished for toggling disguising.");
        }
        }
    }
    @EventHandler
    public void beforeVanishChange(VanishStatusChangeEvent event) {
        if (!this.enabled) {
            return;
        }
        final Player player = event.getPlayer();
        if (DisguiseAPI.isDisguised(player)) {
            DisguiseAPI.undisguiseToAll(player);
            player.sendMessage(ChatColor.AQUA + "You have been undisguised for toggling vanishing.");
        }
    }




}
