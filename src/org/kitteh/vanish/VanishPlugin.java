package org.kitteh.vanish;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.hooks.*;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.listeners.*;
import org.kitteh.vanish.metrics.MetricsOverlord;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

public class VanishPlugin extends JavaPlugin {

    private class UpdateCheck implements Runnable {

        VanishPlugin plugin;

        public UpdateCheck(VanishPlugin vanishPlugin) {
            this.plugin = vanishPlugin;
        }

        @Override
        public void run() {
            try {
                final String address = "http://updates.kitteh.org/VanishNoPacket/version.php?bukkit=" + this.plugin.getServer().getVersion() + "&version=" + this.plugin.getDescription().getVersion() + "&port=" + this.plugin.getServer().getPort();
                final URL url = new URL(address.replace(" ", "%20"));
                final URLConnection connection = url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-agent", "VanishNoPacket " + this.plugin.getDescription().getVersion());
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String version;
                if ((version = bufferedReader.readLine()) != null) {
                    this.plugin.latestVersion = version;
                    if (!this.plugin.getDescription().getVersion().equals(version)) {
                        this.plugin.log("Found a different version available: " + version);
                        this.plugin.log("Check http://dev.bukkit.org/server-mods/vanish/");
                        this.plugin.versionDiff = true;
                    }
                    bufferedReader.close();
                    connection.getInputStream().close();
                    return;
                } else {
                    bufferedReader.close();
                    connection.getInputStream().close();
                }
            } catch (final Exception e) {
            }
            this.plugin.log("Error: Could not check if plugin was up to date. Will try later");
        }

    }

    private final HashSet<String> haveInventoriesOpen = new HashSet<String>();

    private String latestVersion = null;

    private boolean versionDiff = false;

    private final VanishManager manager = new VanishManager(this);

    private HookManager hookManager;

    /**
     * Inform VNP that the user has closed their fake chest
     * 
     * @param name
     *            user's name
     */
    public void chestFakeClose(String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.remove(name);
        }
    }

    /**
     * Query if a user is currently using a fake chest
     * 
     * @param name
     *            user's name
     * @return true if currently using a fake chest
     */
    public boolean chestFakeInUse(String name) {
        synchronized (this.haveInventoriesOpen) {
            return this.haveInventoriesOpen.contains(name);
        }
    }

    /**
     * Inform VNP that the user has opened their fake chest
     * 
     * @param name
     *            user's name
     */
    public void chestFakeOpen(String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.add(name);
        }
    }

    /**
     * Version string of VNP
     * 
     * @return version of VanishNoPacket in use
     */
    public String getCurrentVersion() {
        return this.getDescription().getVersion();
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    /**
     * Will show this version, if update checks are disabled
     * 
     * @return The latest found version of VanishNoPacket
     */
    public String getLatestKnownVersion() {
        return this.latestVersion;
    }

    /**
     * 
     * @return the VanishManager
     */
    public VanishManager getManager() {
        return this.manager;
    }

    /**
     * Indicate a player has just joined the server.
     * Internal use only please
     * 
     * @param player
     *            player who has joined the server
     */
    public void hooksJoin(Player player) {
        this.hookManager.onJoin(player);
    }

    /**
     * Indicate a player has left the server
     * Internal use only please
     * 
     * @param player
     *            player who has left the server
     */
    public void hooksQuit(Player player) {
        this.hookManager.onQuit(player);
        this.hookManager.onUnvanish(player);
    }

    /**
     * No touchy. Call hooks for when a player has unvanished
     * 
     * @param player
     *            The un-vanishing user
     */
    public void hooksUnvanish(Player player) {
        this.hookManager.onUnvanish(player);
    }

    /**
     * No touchy. Call hooks for when player has vanished
     * 
     * @param player
     *            The vanishing player
     */
    public void hooksVanish(Player player) {
        this.hookManager.onVanish(player);
    }

    /**
     * Logs at level INFO prefixed with [Vanish]
     * 
     * @param message
     */
    public void log(String message) {
        this.getLogger().info(message);
    }

    /**
     * Send a message to all players with vanish.statusupdates
     * 
     * @param message
     */
    public void messageStatusUpdate(String message) {
        this.messageStatusUpdate(message, null);
    }

    /**
     * Send a message to all players with vanish.statusupdates but one
     * 
     * @param message
     * @param avoid
     *            Player to not send the message to
     */
    public void messageStatusUpdate(String message, Player avoid) {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.equals(avoid) && VanishPerms.canSeeStatusUpdates(player)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onDisable() {
        for (final Player player : VanishPlugin.this.getServer().getOnlinePlayers()) {
            if (player != null) {
                if (this.manager.isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "[Vanish] You have been forced visible by a reload.");
                }
            }
        }
        VanishNoPacket.setInstance(null);
        this.hookManager.onDisable();
        this.manager.onPluginDisable();
        this.getServer().getScheduler().cancelTasks(this);
        Debuggle.nah();
        this.log("v" + this.getDescription().getVersion() + " unloaded.");
    }

    @Override
    public void onEnable() {
        VanishNoPacket.setInstance(this);

        final File check = new File(this.getDataFolder(), "config.yml");
        boolean firstTimeStarting = false;
        if (!check.exists()) {
            firstTimeStarting = true;
            Settings.deployDefaultConfig("config.yml");
            this.reloadConfig();
        }

        Settings.freshStart(this);
        MetricsOverlord.init(this);

        if (this.getConfig().getBoolean("colornametags", true)) {
            this.getServer().getPluginManager().registerEvents(new TagAPIListener(this), this);
        }

        this.hookManager = new HookManager(this);
        if (this.getConfig().getBoolean("hooks.essentials", false)) {
            this.hookManager.getHook(HookType.Essentials).onEnable();
        }
        this.hookManager.getHook(HookType.GeoIPTools).onEnable();
        if (this.getConfig().getBoolean("hooks.dynmap", false)) {
            this.hookManager.getHook(HookType.Dynmap).onEnable();
        }

        //Post-load stuff
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (VanishPlugin.this.getConfig().getBoolean("hooks.JSONAPI", false)) {
                    VanishPlugin.this.hookManager.getHook(HookType.JSONAPI).onEnable();
                }
            }
        }, 1);

        if (this.getConfig().getBoolean("hooks.spoutcraft", false)) {
            this.hookManager.getHook(HookType.SpoutCraft).onEnable();
        }

        this.manager.startup();

        boolean updateCheck = this.getConfig().getBoolean("checkupdates", true);
        if (firstTimeStarting) {
            updateCheck = false;
            this.log("This is your first time (or you wiped your config).");
            this.log("In future startups, VanishNoPacket will send usage data");
            this.log("and check for updated versions. If you hate useful info,");
            this.log("The setting can be disabled in the config file.");
            this.log("Note that this plugin also utilizes PluginMetrics with usage tracking");
            this.log("If you do not want usage tracking (paranoid) disable in that config");
        }

        this.latestVersion = this.getDescription().getVersion();

        if (updateCheck) {
            if (this.getDescription().getVersion().contains("SNAPSHOT")) {
                this.log("You are using a SNAPSHOT build. Update check disabled");
            } else {
                this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new UpdateCheck(this), 40, 432000);
            }
        }

        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getServer().getPluginManager().registerEvents(new ListenEntity(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerMessages(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerOther(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenToYourHeart(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenInventory(this), this);

        this.log("v" + this.getDescription().getVersion() + " loaded.");
    }

    /**
     * Reload the VNP config
     */
    public void reload() {
        this.reloadConfig();
        Settings.freshStart(this);
    }

    /**
     * Will always be false if update checks are disabled
     * 
     * @return whether or not there's a new version available
     */
    public boolean versionDifference() {
        return this.versionDiff;
    }

}