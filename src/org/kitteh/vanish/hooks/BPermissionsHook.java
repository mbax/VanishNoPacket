package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.info.InfoReader;

public class BPermissionsHook {

    private final VanishPlugin plugin;
    private InfoReader bPerms=null;

    public BPermissionsHook(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPluginEnable() {
        if(this.plugin.getServer().getPluginManager().isPluginEnabled("bPermissions")){
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        BPermissionsHook.this.bPerms = Permissions.getInfoReader();
                    } catch (final Exception e) {
                        BPermissionsHook.this.bPerms = null;
                    }
                }
            });
        }
    }

    public String getPrefix(Player player) {
        if (this.bPerms != null) {
            try {
                return this.bPerms.getPrefix(player);
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getSuffix(Player player) {
        if (this.bPerms != null) {
            try {
                return this.bPerms.getSuffix(player);
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

}
