package org.kitteh.vanish.metrics;

import org.bukkit.plugin.PluginManager;
import org.kitteh.vanish.VanishPlugin;

public final class MetricsOverlord {
    private static Tracker command = new Tracker("");
    private static Tracker vanish = new Tracker("");
    private static Tracker unvanish = new Tracker("");
    private static Tracker toggle = new Tracker("");
    private static Tracker startup = new Tracker("");
    private static Tracker fakejoin = new Tracker("");
    private static Tracker fakequit = new Tracker("");
    private static Tracker quitinvis = new Tracker("");
    private static Tracker joininvis = new Tracker("");
    private static Metrics metrics;

    private static final String[] PERMS_PLUGINS = {"DroxPerms", "GroupManager", "PermissionsBukkit", "PermissionsEx", "Privileges", "SimplyPerms", "Starburst", "bPermissions", "zPermissions"};

    public static Tracker getCommandTracker() {
        return MetricsOverlord.command;
    }

    public static Tracker getFakejoinTracker() {
        return MetricsOverlord.fakejoin;
    }

    public static Tracker getFakequitTracker() {
        return MetricsOverlord.fakequit;
    }

    public static Tracker getJoinInvisTracker() {
        return MetricsOverlord.joininvis;
    }

    public static Tracker getQuitInvisTracker() {
        return MetricsOverlord.quitinvis;
    }

    public static Tracker getToggleTracker() {
        return MetricsOverlord.toggle;
    }

    public static Tracker getUnvanishTracker() {
        return MetricsOverlord.unvanish;
    }

    public static Tracker getVanishTracker() {
        return MetricsOverlord.vanish;
    }

    public static void init(VanishPlugin plugin) {
        MetricsOverlord.command = new Tracker("Command");
        MetricsOverlord.vanish = new Tracker("Vanish");
        MetricsOverlord.unvanish = new Tracker("Unvanish");
        MetricsOverlord.toggle = new Tracker("Toggles");
        MetricsOverlord.startup = new Tracker("Startups");
        MetricsOverlord.startup.increment();
        MetricsOverlord.fakejoin = new Tracker("Fake Joins");
        MetricsOverlord.fakequit = new Tracker("Fake Quits");
        MetricsOverlord.quitinvis = new Tracker("Silent Quit");
        MetricsOverlord.joininvis = new Tracker("Silent Join");
        try {
            MetricsOverlord.metrics = new Metrics(plugin);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.command);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.vanish);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.unvanish);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.toggle);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.startup);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.fakejoin);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.fakequit);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.quitinvis);
            MetricsOverlord.metrics.addCustomData(MetricsOverlord.joininvis);
            MetricsOverlord.metrics.createGraph("Permissions").addPlotter(new Counter(MetricsOverlord.getPermsPlugin(plugin)));
            MetricsOverlord.metrics.createGraph("Online Mode").addPlotter(new Counter(plugin.getServer().getOnlineMode() ? "Online" : "Offline"));
            MetricsOverlord.metrics.createGraph("TagAPI").addPlotter(new Counter(plugin.getServer().getPluginManager().isPluginEnabled("TagAPI") ? "Yup" : "Nope"));
            MetricsOverlord.metrics.start();
        } catch (final Exception e) {
        }
    }

    private static String getPermsPlugin(VanishPlugin plugin) {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (final String permsplugin : MetricsOverlord.PERMS_PLUGINS) {
            if (pluginManager.isPluginEnabled(permsplugin)) {
                return permsplugin;
            }
        }
        return "Unknown or None";
    }
}