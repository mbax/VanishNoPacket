package org.kitteh.vanish;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.compat.NMSManager;
import org.kitteh.vanish.hooks.HookManager;
import org.kitteh.vanish.hooks.HookManager.HookType;
import org.kitteh.vanish.listeners.ListenEntity;
import org.kitteh.vanish.listeners.ListenInventory;
import org.kitteh.vanish.listeners.ListenPlayerJoin;
import org.kitteh.vanish.listeners.ListenPlayerMessages;
import org.kitteh.vanish.listeners.ListenPlayerOther;
import org.kitteh.vanish.listeners.ListenToYourHeart;
import org.kitteh.vanish.listeners.TagAPIListener;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

public final class VanishPlugin extends JavaPlugin {
    private final class UpdateCheck implements Runnable {
        private static final String CURRENT_VERSION = "v" + "${vnp-version}";

        private final VanishPlugin plugin;

        private UpdateCheck(VanishPlugin vanishPlugin) {
            this.plugin = vanishPlugin;
        }

        @Override
        public void run() {
            // Thank you Gravity, for your neat updater code
            String latest = null;
            InputStream inputStream = null;
            try {
                inputStream = new URL("http://dev.bukkit.org/server-mods/vanish/files.rss").openStream();
            } catch (final IOException e) {
                this.plugin.getLogger().warning("Could not reach BukkitDev file stream for update checking. Is dev.bukkit.org offline?");
                latest = null;
            }
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            if (inputStream != null) {
                try {
                    final XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
                    String maybeLatest = null;
                    while (eventReader.hasNext()) {
                        XMLEvent event = eventReader.nextEvent();
                        if (event.isStartElement()) {
                            if (event.asStartElement().getName().getLocalPart().equals("title")) {
                                event = eventReader.nextEvent();
                                maybeLatest = event.asCharacters().getData();
                            }
                        } else if (event.isEndElement()) {
                            if (event.asEndElement().getName().getLocalPart().equals("item")) {
                                latest = maybeLatest;
                                break;
                            }
                        }
                    }
                } catch (final XMLStreamException e) {
                    this.plugin.getLogger().warning("Could not reach dev.bukkit.org for update checking. Is it offline?");
                }
            }
            if (latest != null) {
                this.plugin.latestVersion = latest;
                if (!UpdateCheck.CURRENT_VERSION.equals(latest)) {
                    this.plugin.getLogger().info("Found a different version available: " + latest);
                    this.plugin.getLogger().info("Check http://www.curse.com/server-mods/minecraft/vanish");
                    this.plugin.versionDiff = true;
                }
            } else {
                this.plugin.getLogger().info("Error: Could not check if plugin was up to date. Will try later");
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
        return "${project.version}";
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
        VanishNoPacket.setInstance(null);
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
        this.getLogger().info("v${project.version} unloaded.");
    }

    @Override
    public void onEnable() {
        NMSManager.load(this);
        VanishNoPacket.setInstance(this);

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

        if (this.getConfig().getBoolean("colornametags", true)) {
            if (this.getServer().getPluginManager().isPluginEnabled("TagAPI")) {
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
        if (this.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            this.hookManager.getHook(HookType.ProtocolLib).onEnable();
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

        this.manager = new VanishManager(this);

        for (final Player player : this.getServer().getOnlinePlayers()) {
            player.setMetadata("vanished", new LazyMetadataValue(this, CacheStrategy.NEVER_CACHE, new VanishCheck(player.getName())));
        }

        boolean updateCheck = this.getConfig().getBoolean("checkupdates", true);
        if (firstTimeStarting) {
            updateCheck = false;
            this.getLogger().info("This is your first startup (or you wiped your config).");
            this.getLogger().info("In future startups, VanishNoPacket will check for updates");
            this.getLogger().info("If you dislike it, disable 'checkupdates' in the config file");
        }

        if (updateCheck) {
            if (this.getCurrentVersion().contains("SNAPSHOT") || this.getCurrentVersion().equals("${project" + ".version}") || this.getCurrentVersion().endsWith("unofficial")) {
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

        this.getLogger().info("v${project.version} loaded.");
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
}