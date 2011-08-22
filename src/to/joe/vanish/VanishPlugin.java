package to.joe.vanish;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class VanishPlugin extends JavaPlugin {

    private final VanishManager manager = new VanishManager(this);

    private final ListenEntity listenEntity = new ListenEntity(this);
    private final ListenPlayer listenPlayer = new ListenPlayer(this);

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
        this.log("Version " + this.selfDescription.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {

        final Configuration config = this.getConfiguration();
        this.enableColoration = config.getBoolean("enableColoration", false);
        config.save();

        this.selfDescription = this.getDescription();

        this.manager.reset();

        this.log = Logger.getLogger("Minecraft");

        this.getCommand("vanish").setExecutor(new VanishCommand(this));

        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, this.listenEntity, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listenPlayer, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this.listenPlayer, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.listenPlayer, Priority.Highest, this);

        this.log("Version " + this.selfDescription.getVersion() + " enabled.");
    }
}