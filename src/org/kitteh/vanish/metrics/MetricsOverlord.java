package org.kitteh.vanish.metrics;

import org.kitteh.vanish.VanishPlugin;

public class MetricsOverlord {
    public static Tracker command;
    public static Tracker vanish;
    public static Tracker unvanish;
    public static Tracker toggle;
    public static Tracker startup;
    public static Tracker fakejoin;
    public static Tracker fakequit;
    public static Tracker quitinvis;
    public static Tracker joininvis;
    public static Metrics metrics;

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
            MetricsOverlord.metrics = new Metrics();
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.command);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.vanish);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.unvanish);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.toggle);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.startup);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.fakejoin);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.fakequit);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.quitinvis);
            MetricsOverlord.metrics.addCustomData(plugin, MetricsOverlord.joininvis);
            MetricsOverlord.metrics.beginMeasuringPlugin(plugin);
        } catch (final Exception e) {
        }
    }
}
