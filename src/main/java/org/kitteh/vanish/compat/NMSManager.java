package org.kitteh.vanish.compat;

import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.compat.api.NMSCallProvider;

public final class NMSManager {
    private static NMSCallProvider provider;

    public static NMSCallProvider getProvider() {
        return NMSManager.provider;
    }

    public static void load(Plugin plugin) {
        final String packageName = plugin.getServer().getClass().getPackage().getName();
        String cbversion = packageName.substring(packageName.lastIndexOf('.') + 1);
        if (cbversion.equals("craftbukkit")) {
            cbversion = "pre";
        }
        try {
            final Class<?> clazz = Class.forName("org.kitteh.vanish.compat." + cbversion + ".NMSHandler");
            if (NMSCallProvider.class.isAssignableFrom(clazz)) {
                NMSManager.provider = (NMSCallProvider) clazz.getConstructor().newInstance();
            } else {
                throw new Exception("Nope");
            }
        } catch (final Exception e) {
            plugin.getLogger().severe("Could not find support for this CraftBukkit version.");
            plugin.getLogger().info("Check for updates at http://dev.bukkit.org/server-mods/vanish");
            plugin.getLogger().info("Will attempt things without, might be buggy!");
            NMSManager.provider = new FailedHandler();
            return;
        }
        plugin.getLogger().info("Loading support for " + (cbversion.equals("pre") ? "1.4.5-pre-RB" : cbversion));
    }
}