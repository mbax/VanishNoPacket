package to.joe.vanish;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import to.joe.vanish.hooks.DynmapHook;
import to.joe.vanish.hooks.EssentialsHook;
import to.joe.vanish.hooks.JSONAPIHook;
import to.joe.vanish.listeners.*;

@SuppressWarnings("deprecation")
public class VanishPlugin extends JavaPlugin {

    private class UpdateCheck implements Runnable {

        VanishPlugin plugin;

        public UpdateCheck(VanishPlugin vanishPlugin) {
            this.plugin = vanishPlugin;
        }

        @Override
        public void run() {
            URL url;
            URLConnection connection;
            try {
                String address="http://updates.kitteh.org/VanishNoPacket/version.php?bukkit=" + this.plugin.getServer().getVersion() + "&version=" + VanishPlugin.this.selfDescription.getVersion() + "&port=" + this.plugin.getServer().getPort();
                url = new URL(address.replace(" ", "%20"));
                connection = url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-agent", "VanishNoPacket " + VanishPlugin.this.selfDescription.getVersion());
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String version;
                if ((version = bufferedReader.readLine()) != null) {
                    this.plugin.latestVersion = version;
                    if (!this.plugin.selfDescription.getVersion().equals(version)) {
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

    public String latestVersion = null;
    public boolean versionDiff = false;

    private final VanishManager manager = new VanishManager(this);

    private final ListenEntity listenEntity = new ListenEntity(this);
    private final ListenPlayer listenPlayer = new ListenPlayer(this);
    private final ListenPlayerJoinEarly listenPlayerJoinEarly = new ListenPlayerJoinEarly(this);
    private final ListenPlayerJoinLate listenPlayerJoinLate = new ListenPlayerJoinLate(this);
    private final ListenPlayerCommandPreProcess listenPlayerCommandPreProcess = new ListenPlayerCommandPreProcess(this);

    private final EssentialsHook essentialsHook = new EssentialsHook(this);
    private final DynmapHook dynmapHook = new DynmapHook(this);
    private final JSONAPIHook jsonapiHook = new JSONAPIHook(this);

    public PluginDescriptionFile selfDescription;

    private Logger log;

    private boolean enableColoration;

    public boolean colorationEnabled() {
        return this.enableColoration;
    }

    /**
     * Please, sir, can I have some more?
     * 
     * @return the VanishManager. Duh.
     */
    public VanishManager getManager() {
        return this.manager;
    }

    public void hooksUnvanish(Player player) {
        this.essentialsHook.unvanish(player);
        this.dynmapHook.unvanish(player);
    }

    public void hooksVanish(Player player) {
        this.essentialsHook.vanish(player);
        this.dynmapHook.vanish(player);
    }

    /**
     * Tag that log!
     * 
     * @param message
     */
    public void log(String message) {
        this.log.info("[VANISH] " + message);
    }

    public void messageUpdate(String message) {
        this.messageUpdate(message, null);
    }

    public void messageUpdate(String message, Player avoid) {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            if ((player != null) && !player.equals(avoid) && VanishPerms.canSeeStatusUpdates(player)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onDisable() {
        this.essentialsHook.onPluginDisable();
        this.dynmapHook.onPluginDisable();
        this.manager.disable();
        this.getServer().getScheduler().cancelTasks(this);
        this.log("Version " + this.selfDescription.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        this.log = Logger.getLogger("Minecraft");
        this.selfDescription = this.getDescription();

        final File check = new File("plugins/VanishNoPacket/config.yml");
        boolean firstTime = false;
        if (!check.exists()) {
            firstTime = true;
        }
        //final FileConfiguration config=this.getConfig();
        final Configuration config=this.getConfiguration();
        //config.options().copyDefaults(true);
        this.enableColoration = config.getBoolean("enableColoration", false);
        this.essentialsHook.onPluginEnable(config.getBoolean("hooks.essentials", false));
        this.dynmapHook.onPluginEnable(config.getBoolean("hooks.dynmap", false));
        this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){ public void run() {jsonapiHook.onPluginEnable(config.getBoolean("hooks.JSONAPI", false)); }}, 80);
        

        this.manager.startup(config.getString("fakeannounce.join", "%p joined the game."), config.getString("fakeannounce.quit", "%p left the game."), config.getBoolean("fakeannounce.automaticforsilentjoin", false));
        boolean updateCheck = config.getBoolean("updates.check", true);
        if (firstTime) {
            updateCheck = false;
            this.log("This is your first time (or you wiped your config)");
            this.log("In future startups, VanishNoPacket will send usage data");
            this.log("and check for updated versions. If you hate useful info");
            this.log("The setting can be disabled in the config file");
        }
        if (this.latestVersion != null) {
            this.latestVersion = this.selfDescription.getVersion();
        }
        if (updateCheck) {
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new UpdateCheck(this), 40, 432000);
        }
        this.listenPlayerCommandPreProcess.setEnabled(config.getBoolean("permtest.enable", false));

        config.save();
        //this.saveConfig();

        this.getCommand("vanish").setExecutor(new VanishCommand(this));

        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.listenPlayerCommandPreProcess, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.listenPlayer, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinLate, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinEarly, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.listenPlayer, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this.listenPlayer, Priority.Normal, this);

        this.log("Version " + this.selfDescription.getVersion() + " enabled.");
    }
    
    /**
     * Ah the things I do for APIs
     * @param player name
     * @return if player is vanished
     */
    public boolean isVanished(String player){
        return this.getManager().isVanished(player);
    }
}