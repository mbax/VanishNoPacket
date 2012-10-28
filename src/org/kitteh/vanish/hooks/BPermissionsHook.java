package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

public class BPermissionsHook extends Hook {

    public BPermissionsHook(VanishPlugin plugin) {
        super(plugin);
    }

    public String getPrefix(Player player) {
        String result = null;
        if (this.bPermsEnabled()) {
            try {
                result = this.getValue(player, "prefix");
            } catch (final Exception e) {
            }
        }
        return result != null ? result : "";
    }

    public String getSuffix(Player player) {
        String result = null;
        if (this.bPermsEnabled()) {
            try {
                result = this.getValue(player, "suffix");
            } catch (final Exception e) {
            }
        }
        return result != null ? result : "";
    }

    private boolean bPermsEnabled() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled("bPermissions");
    }

    private String getValue(Player player, String key) {
        return ApiLayer.getValue(player.getWorld().getName(), CalculableType.USER, player.getName(), key);
    }

}
