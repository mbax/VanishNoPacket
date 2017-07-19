package org.kitteh.vanish;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kitteh.vanish.hooks.HookManager;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.listeners.ListenEntity;
import org.kitteh.vanish.listeners.ListenInventory;
import org.kitteh.vanish.listeners.ListenPlayerJoin;
import org.kitteh.vanish.listeners.ListenPlayerMessages;
import org.kitteh.vanish.listeners.ListenPlayerOther;
import org.kitteh.vanish.listeners.ListenServerPing;
import org.kitteh.vanish.listeners.ListenToYourHeart;
import org.kitteh.vanish.listeners.TagAPIListener;
import org.kitteh.vanish.metrics.MetricsOverlord;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

public final class VanishPlugin extends JavaPlugin {
    final class UpdateCheck implements Runnable {
        private static final String CREDITS = "This updater code is based on the great work of Gravity";

        String getCredits() {
            return CREDITS;
        }

        private final VanishPlugin plugin;

        private UpdateCheck(VanishPlugin vanishPlugin) {
            this.plugin = vanishPlugin;
        }

        @Override
        public void run() {
            final File pluginsFolder = this.plugin.getDataFolder().getParentFile();
            final File updaterFolder = new File(pluginsFolder, "Updater");
            final File updaterConfigFile = new File(updaterFolder, "config.yml");
            String apiKey = null;
            String latest = null;

            if (updaterFolder.exists()) {
                if (updaterConfigFile.exists()) {
                    final YamlConfiguration config = YamlConfiguration.loadConfiguration(updaterConfigFile);
                    apiKey = config.getString("api-key");
                }
            }

            URL url;
            try {
                url = new URL("https://api.curseforge.com/servermods/files?projectIds=30949");
            } catch (final MalformedURLException e) {
                return;
            }

            URLConnection conn;
            IOException exceptional = null;
            try {
                conn = url.openConnection();

                conn.setConnectTimeout(5000);
                if (apiKey != null) {
                    conn.addRequestProperty("X-API-Key", apiKey);
                }
                conn.addRequestProperty("User-Agent", "KittehUpdater (by mbaxter)");
                conn.setDoOutput(true);

                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String response = reader.readLine();

                final JSONArray array = (JSONArray) JSONValue.parse(response);
                if (array.size() == 0) {
                    return;
                }

                latest = (String) ((JSONObject) array.get(array.size() - 1)).get("name");
            } catch (final IOException e) {
                exceptional = e;
            }
            if (latest != null) {
                this.plugin.latestVersion = latest;
                if (!("v" + this.plugin.getCurrentVersion()).equals(latest)) {
                    this.plugin.getLogger().info("Found a different version available: " + latest);
                    this.plugin.getLogger().info("Check http://www.curse.com/server-mods/minecraft/vanish");
                    this.plugin.versionDiff = true;
                }
            } else {
                this.plugin.getLogger().info("Error: Could not check if plugin was up to date. Will try later");
                if (exceptional != null) {
                    this.plugin.getLogger().info("Exception message: " + exceptional.getMessage());
                }
            }
        }
    }

    private final HashSet<String> haveInventoriesOpen = new HashSet<String>();
    private String latestVersion = null;
    private boolean versionDiff = false;
    private VanishManager manager;
    private final HookManager hookManager = new HookManager(this);

    /**
     * Informs VNP that a user has closed their fake chest
     *
     * @param name user's name
     */
    public void chestFakeClose(String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.remove(name);
        }
    }

    /**
     * Queries if a user is currently using a fake chest
     *
     * @param name the user's name
     * @return true if currently using a fake chest
     */
    public boolean chestFakeInUse(String name) {
        synchronized (this.haveInventoriesOpen) {
            return this.haveInventoriesOpen.contains(name);
        }
    }

    /**
     * Informs VNP that a user has opened their fake chest
     *
     * @param name user's name
     */
    public void chestFakeOpen(String name) {
        synchronized (this.haveInventoriesOpen) {
            this.haveInventoriesOpen.add(name);
        }
    }

    /**
     * Gets the current version
     *
     * @return version of VanishNoPacket in use
     */
    public String getCurrentVersion() {
        return this.getDescription().getVersion();
    }

    /**
     * Gets the hook manager
     *
     * @return the hook manager
     */
    public HookManager getHookManager() {
        return this.hookManager;
    }

    /**
     * Gets the latest found version
     * Will show this version, if update checks are disabled
     *
     * @return the latest found version of VanishNoPacket
     */
    public String getLatestKnownVersion() {
        return this.latestVersion;
    }

    /**
     * Gets the vanish manager
     *
     * @return the VanishManager
     */
    public VanishManager getManager() {
        return this.manager;
    }

    /**
     * Indicates a player has just joined the server.
     * Internal use only please
     *
     * @param player player who has joined the server
     */
    public void hooksJoin(Player player) {
        this.hookManager.onJoin(player);
    }

    /**
     * Indicates a player has left the server
     * Internal use only please
     *
     * @param player player who has left the server
     */
    public void hooksQuit(Player player) {
        this.hookManager.onQuit(player);
        this.hookManager.onUnvanish(player);
    }

    /**
     * Calls hooks for when a player has unvanished
     * Internal use only please
     *
     * @param player the un-vanishing user
     */
    public void hooksUnvanish(Player player) {
        this.hookManager.onUnvanish(player);
    }

    /**
     * Calls hooks for when player has vanished
     * Internal use only please.
     *
     * @param player the vanishing player
     */
    public void hooksVanish(Player player) {
        this.hookManager.onVanish(player);
    }

    /**
     * Sends a message to all players with vanish.statusupdates permission
     *
     * @param message the message to send
     */
    public void messageStatusUpdate(String message) {
        this.messageStatusUpdate(message, null);
    }

    /**
     * Sends a message to all players with vanish.statusupdates but one
     *
     * @param message the message to send
     * @param avoid player to not send the message to
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
        this.setInstance(null);
        Debuggle.nah();
        for (final Player player : VanishPlugin.this.getServer().getOnlinePlayers()) {
            if (player != null) {
                if (this.manager.isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "[Vanish] You have been forced visible by a reload.");
                }
            }
        }
        this.hookManager.onDisable();
        this.manager.onPluginDisable();
        this.getLogger().info(this.getCurrentVersion() + " unloaded.");
    }

    @Override
    public void onEnable() {
        this.setInstance(this);

        final File check = new File(this.getDataFolder(), "config.yml");
        boolean firstTimeStarting = false;
        if (!check.exists()) {
            firstTimeStarting = true;
            this.saveDefaultConfig();
            this.reloadConfig();
            if (this.getServer().getPluginManager().isPluginEnabled("Essentials")) {
                this.getConfig().set("hooks.essentials", true);
                this.getLogger().info("Detected Essentials. Enabling Essentials hook.");
                this.saveConfig();
            }
        }

        Settings.freshStart(this);

        dance:
        if (this.getConfig().getBoolean("colornametags", true)) {
            if (this.getServer().getPluginManager().isPluginEnabled("TagAPI")) {
                try {
                    Class.forName("org.kitteh.tag.AsyncPlayerReceiveNameTagEvent");
                } catch (final ClassNotFoundException e) {
                    this.getLogger().warning("Update to TagAPI 3.0 or later to use name coloring");
                    break dance;
                }
                this.getServer().getPluginManager().registerEvents(new TagAPIListener(this), this);
                this.getLogger().info("Using color changing features of TagAPI.");
            } else {
                this.getLogger().info("Colored nametags enabled, but I couldn't find TagAPI");
                this.getLogger().info("For awesome colored nametags on vanish, visit");
                this.getLogger().info("http://dev.bukkit.org/server-mods/tag/ ");
                this.getLogger().info("and download TagAPI.jar");
            }
        }

        if (this.getConfig().getBoolean("hooks.essentials", false)) {
            this.hookManager.getHook(HookType.Essentials).onEnable();
        }
        this.hookManager.getHook(HookType.GeoIPTools).onEnable();
        if (this.getConfig().getBoolean("hooks.dynmap", false)) {
            this.hookManager.getHook(HookType.Dynmap).onEnable();
        }
        //if (this.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
        //    this.hookManager.getHook(HookType.ProtocolLib).onEnable();
        //}

        final VanishPlugin self = this;
        //Post-load stuff
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (VanishPlugin.this.getConfig().getBoolean("hooks.JSONAPI", false)) {
                    VanishPlugin.this.hookManager.getHook(HookType.JSONAPI).onEnable();
                }
                MetricsOverlord.init(self);
            }
        }, 1);

        this.manager = new VanishManager(this);

        for (final Player player : this.getServer().getOnlinePlayers()) {
            player.setMetadata("vanished", new LazyMetadataValue(this, CacheStrategy.NEVER_CACHE, new VanishCheck(this.manager, player.getName())));
        }

        boolean updateCheck = this.getConfig().getBoolean("checkupdates", true);
        if (firstTimeStarting) {
            updateCheck = false;
            this.getLogger().info("This is your first startup (or you wiped your config).");
            this.getLogger().info("In future startups, VanishNoPacket will check for updates");
            this.getLogger().info("If you dislike it, disable 'checkupdates' in the config file");
            this.getLogger().info("Note that this plugin also utilizes PluginMetrics with usage tracking");
            this.getLogger().info("If you do not want usage tracking (paranoid) disable in that config");
        }

        if (updateCheck) {
            if (this.getCurrentVersion().contains("SNAPSHOT") || this.getCurrentVersion().equals("${project.version}") || this.getCurrentVersion().endsWith("unofficial")) {
                this.getLogger().info("Not a release version. Update check disabled");
            } else {
                this.getServer().getScheduler().runTaskTimerAsynchronously(this, new UpdateCheck(this), 40, 432000);
            }
        }

        this.getCommand("vanish").setExecutor(new VanishCommand(this));
        this.getServer().getPluginManager().registerEvents(new ListenEntity(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerMessages(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenPlayerOther(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenToYourHeart(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenInventory(this), this);
        this.getServer().getPluginManager().registerEvents(new ListenServerPing(this.manager), this);

        this.getLogger().info(this.getCurrentVersion() + " loaded.");
    }

    /**
     * Reloads the VNP config
     */
    public void reload() {
        this.reloadConfig();
        Settings.freshStart(this);
    }

    /**
     * Gets if there is a difference in versions between this and latest
     * Will always be false if update checks are disabled
     *
     * @return whether or not there's a new version available
     */
    public boolean versionDifference() {
        return this.versionDiff;
    }

    @SuppressWarnings("deprecation")
    private void setInstance(VanishPlugin plugin) {
        org.kitteh.vanish.staticaccess.VanishNoPacket.setInstance(plugin);
    }
}