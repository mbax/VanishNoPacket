package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import uk.org.whoami.geoip.GeoIPLookup;
import uk.org.whoami.geoip.GeoIPTools;

public class GeoIPToolsHook {

    private final VanishPlugin plugin;
    private GeoIPLookup city;
    private GeoIPLookup country;

    public GeoIPToolsHook(VanishPlugin plugin) {
        this.plugin = plugin;
        this.city = null;
        this.country = null;
    }

    public String getCity(Player player) {
        if (this.city != null) {
            try {
                return this.city.getLocation(player.getAddress().getAddress()).city;
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getCountry(Player player) {
        if (this.country != null) {
            try {
                return this.country.getCountry(player.getAddress().getAddress()).getName();
            } catch (final Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public void onPluginEnable() {
        final Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("GeoIPTools");
        if (plugin != null) {
            final GeoIPTools geoip = (GeoIPTools) plugin;
            this.city = geoip.getGeoIPLookup(GeoIPLookup.CITYDATABASE);
            this.country = geoip.getGeoIPLookup(GeoIPLookup.COUNTRYDATABASE);
        }
    }

}
