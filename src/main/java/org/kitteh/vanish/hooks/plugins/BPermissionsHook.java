package org.kitteh.vanish.hooks.plugins;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.ChatProvider;
import org.kitteh.vanish.hooks.Hook;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

@SuppressWarnings("deprecation")
public final class BPermissionsHook extends Hook implements ChatProvider {
    public BPermissionsHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
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

    @Override
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