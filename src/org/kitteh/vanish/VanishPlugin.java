package org.kitteh.vanish;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.kitteh.vanish.hooks.BPermissionsHook;
import org.kitteh.vanish.hooks.DynmapHook;
import org.kitteh.vanish.hooks.EssentialsHook;
import org.kitteh.vanish.hooks.JSONAPIHook;
import org.kitteh.vanish.listeners.*;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.util.DefaultConfig;

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

    private String latestVersion = null;

    private boolean versionDiff = false;

    private final VanishManager manager = new VanishManager(this);
    private final ListenEntity listenEntity = new ListenEntity(this);

    private final ListenPlayerOther listenPlayer = new ListenPlayerOther(this);

    private final ListenPlayerJoinEarly listenPlayerJoinEarly = new ListenPlayerJoinEarly(this);
    private final ListenPlayerJoinLate listenPlayerJoinLate = new ListenPlayerJoinLate(this);
    private final ListenPlayerMessages listenPlayerMessagesSent = new ListenPlayerMessages(this);
    private final ListenServer listenServer = new ListenServer(this);
    private final ListenSpout listenSpout = new ListenSpout(this);

    private final EssentialsHook essentialsHook = new EssentialsHook(this);
    private final DynmapHook dynmapHook = new DynmapHook(this);
    private final JSONAPIHook jsonapiHook = new JSONAPIHook(this);
    private final VanishSpoutCraft spoutCraft = new VanishSpoutCraft(this);
    private final BPermissionsHook bPermissionsHook = new BPermissionsHook(this);

    private boolean enableColoration;

    /**
     * @return whether or not the hacky packet user coloration is enabled
     */
    public boolean colorationEnabled() {
        return this.enableColoration;
    }

    /**
     * @return version of VanishNoPacket in use
     */
    public String getCurrentVersion() {
        return this.getDescription().getVersion();
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
     * No touchy. Call hooks for when a player has quit
     * 
     * @param player
     */
    public void hooksQuit(Player player) {
        this.hooksUnvanish(player);
        this.spoutCraft.playerQuit(player);
    }

    /**
     * No touchy.
     * Called when a player's spoutcraft client authenticates
     * 
     * @param player
     */
    public void hooksSpoutAuth(SpoutPlayer player) {
        this.spoutCraft.playerHasSpout(player);
    }

    /**
     * No touchy. Call hooks for when a player has unvanished
     * 
     * @param player
     *            The un-vanishing user
     */
    public void hooksUnvanish(Player player) {
        this.essentialsHook.unvanish(player);
        this.dynmapHook.unvanish(player);
        this.spoutCraft.unvanish(player);
    }

    /**
     * No touchy. Call hooks for when player has vanished
     * 
     * @param player
     *            The vanishing player
     */
    public void hooksVanish(Player player) {
        this.essentialsHook.vanish(player);
        this.dynmapHook.vanish(player);
        this.spoutCraft.vanish(player);
    }

    /**
     * Logs at level INFO prefixed with [Vanish]
     * 
     * @param message
     */
    public void log(String message) {
        this.getServer().getLogger().info("[Vanish] " + message);
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
        VanishNoPacket.setInstance(null);
        this.spoutCraft.onPluginDisable();
        this.essentialsHook.onPluginDisable();
        this.dynmapHook.onPluginDisable();
        this.manager.onPluginDisable();
        this.getServer().getScheduler().cancelTasks(this);
        this.log("Version " + this.getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {

        final File check = new File("plugins/VanishNoPacket/config.yml");
        boolean firstTimeStarting = false;
        if (!check.exists()) {
            firstTimeStarting = true;
            DefaultConfig.set("config.yml");
            this.reloadConfig();
        }

        this.enableColoration = this.getConfig().getBoolean("enableColoration", false);

        this.essentialsHook.onPluginEnable(this.getConfig().getBoolean("hooks.essentials", false));

        this.dynmapHook.onPluginEnable(this.getConfig().getBoolean("hooks.dynmap", false));

        this.bPermissionsHook.onPluginEnable();

        //Post-load stuff
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                VanishPlugin.this.jsonapiHook.onPluginEnable(VanishPlugin.this.getConfig().getBoolean("hooks.JSONAPI", false));
                for (final Player player : VanishPlugin.this.getServer().getOnlinePlayers()) {
                    if ((player != null) && VanishPerms.canVanish(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "[VANISH] You have been forced visible by a reload.");
                    }
                }
            }
        }, 1);

        this.spoutCraft.onPluginEnable(this.getConfig().getBoolean("spoutcraft.enable", false));

        this.manager.startup(this.getConfig().getString("fakeannounce.join", "%p joined the game."), this.getConfig().getString("fakeannounce.quit", "%p left the game."), this.getConfig().getBoolean("fakeannounce.automaticforsilentjoin", false), this.getConfig().getBoolean("enableTabControl", true));

        boolean updateCheck = this.getConfig().getBoolean("updates.check", true);
        if (firstTimeStarting) {
            updateCheck = false;
            this.log("This is your first time (or you wiped your config).");
            this.log("In future startups, VanishNoPacket will send usage data");
            this.log("and check for updated versions. If you hate useful info,");
            this.log("The setting can be disabled in the config file.");
        }

        this.latestVersion = this.getDescription().getVersion();

        if (updateCheck) {
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new UpdateCheck(this), 40, 432000);
        }

        this.listenPlayerMessagesSent.setPermTestEnabled(this.getConfig().getBoolean("permtest.enable", false));

        this.getCommand("vanish").setExecutor(new VanishCommand(this));

        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.listenPlayerMessagesSent, Priority.Lowest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, this.listenPlayerMessagesSent, Priority.Lowest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.listenPlayer, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinLate, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinEarly, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.listenPlayer, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this.listenPlayer, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.SERVER_LIST_PING, this.listenServer, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, this.listenSpout, Priority.Normal, this);

        VanishNoPacket.setInstance(this);

        this.log("Version " + this.getDescription().getVersion() + " enabled.");
    }

    public BPermissionsHook getBPerms() {
        return this.bPermissionsHook;
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