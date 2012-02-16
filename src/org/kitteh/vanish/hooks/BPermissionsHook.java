package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public class BPermissionsHook {

    private final VanishPlugin plugin;

    public BPermissionsHook(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public String getPrefix(Player player) {
        if (this.bPermsEnabled()) {
            try {
                return this.getValue(player, "prefix");
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getSuffix(Player player) {
        if (this.bPermsEnabled()) {
            try {
                return this.getValue(player, "suffix");
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    private boolean bPermsEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled("bPermissions");
    }

    private String getValue(Player player, String key) {
        return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), key);
    }

}
