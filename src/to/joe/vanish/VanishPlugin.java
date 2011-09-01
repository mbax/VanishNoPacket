package to.joe.vanish;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import to.joe.vanish.hooks.EssentialsHook;

public class VanishPlugin extends JavaPlugin {

    private final VanishManager manager = new VanishManager(this);

    private final ListenEntity listenEntity = new ListenEntity(this);
    private final ListenPlayer listenPlayer = new ListenPlayer(this);
    private final ListenPlayerJoinEarly listenPlayerJoinEarly = new ListenPlayerJoinEarly(this);
    private final ListenPlayerJoinLate listenPlayerJoinLate = new ListenPlayerJoinLate(this);

    private final EssentialsHook essentialsHook = new EssentialsHook(this);

    private PluginDescriptionFile selfDescription;

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
    }

    public void hooksVanish(Player player) {
        this.essentialsHook.vanish(player);
    }

    /**
     * Tag that log!
     * 
     * @param message
     */
    public void log(String message) {
        this.log.info("[VANISH] " + message);
    }

    public void messageSeers(String message) {
        for (final Player player : this.getServer().getOnlinePlayers()) {
            if ((player != null) && Perms.canSeeAll(player)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onDisable() {
        this.essentialsHook.onPluginDisable();
        this.manager.disable();
        this.log("Version " + this.selfDescription.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        this.log = Logger.getLogger("Minecraft");
        
        final Configuration config = this.getConfiguration();
        this.enableColoration = config.getBoolean("enableColoration", false);
        this.essentialsHook.onPluginEnable(config.getBoolean("hooks.essentials", false));
        config.save();

        this.selfDescription = this.getDescription();

        this.manager.startup();

        this.getCommand("vanish").setExecutor(new VanishCommand(this));

        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinLate, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayerJoinEarly, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this.listenPlayer, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.listenPlayer, Priority.Highest, this);

        this.log("Version " + this.selfDescription.getVersion() + " enabled.");
    }
}