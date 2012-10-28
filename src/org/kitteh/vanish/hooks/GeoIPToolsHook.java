package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import uk.org.whoami.geoip.GeoIPLookup;
import uk.org.whoami.geoip.GeoIPTools;

public class GeoIPToolsHook extends Hook {

    private GeoIPLookup geoip = null;

    public GeoIPToolsHook(VanishPlugin plugin) {
        super(plugin);
    }

    public String getCity(Player player) {
        String result = null;
        if (this.geoip != null) {
            try {
                result = this.geoip.getLocation(player.getAddress().getAddress()).city;
            } catch (final Exception e) {
            }
        }
        return result != null ? result : "";
    }

    public String getCountry(Player player) {
        String result = null;
        if (this.geoip != null) {
            try {
                result = this.geoip.getCountry(player.getAddress().getAddress()).getName();
            } catch (final Exception e) {
            }
        }
        return result != null ? result : "";
    }

    @Override
    public void onDisable() {
        this.geoip = null;
    }

    @Override
    public void onEnable() {
        final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("GeoIPTools");
        if (plugin != null) {
            final GeoIPTools geoip = (GeoIPTools) plugin;
            this.geoip = geoip.getGeoIPLookup();
        }
    }

}
