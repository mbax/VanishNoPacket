package to.joe.vanish;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class VanishSpoutCraft {

    private class PlayerData {
        public String skin, cloak, title;

        public PlayerData(String skin, String cloak, String title) {
            this.skin = skin;
            this.cloak = cloak;
            this.title = title;
        }
    }

    private boolean enabled;

    private final VanishPlugin plugin;

    private final HashMap<String, String> cloaks;
    private final HashMap<String, String> skins;

    private final HashMap<String, String> titles;

    private HashMap<String, PlayerData> playerData;

    public VanishSpoutCraft(VanishPlugin plugin) {
        this.plugin = plugin;

        this.cloaks = new HashMap<String, String>();
        this.skins = new HashMap<String, String>();
        this.titles = new HashMap<String, String>();
    }

    public void onPluginEnable(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.init();
        }
    }

    public void playerHasSpout(SpoutPlayer newPlayer) {
        if (!this.enabled || !VanishPerms.canSeeAll(newPlayer)) {
            return;
        }
        final SpoutPlayer sPlayer = SpoutManager.getPlayer(newPlayer);
        for (final SpoutPlayer p : SpoutManager.getOnlinePlayers()) {
            if (this.plugin.isVanished(p.getName())) {
                PlayerData data = this.playerData.get(p.getName());
                if (data == null) {
                    data = this.initPlayer(p);
                }
                this.playerUpdate(p, data, sPlayer);
            }
        }
        if (this.plugin.isVanished(newPlayer.getName())) {
            this.vanish(newPlayer);
        }
    }

    public void unvanish(Player revealing) {
        if (!this.enabled) {
            return;
        }
        for (final SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
            if ((player != null) && player.hasPermission("vanish.see") && player.isSpoutCraftEnabled()) {
                SpoutManager.getAppearanceManager().resetPlayerSkin(player, revealing);
                SpoutManager.getAppearanceManager().resetPlayerCloak(player, revealing);
                SpoutManager.getAppearanceManager().resetPlayerTitle(player, revealing);
            }
        }
    }

    public void vanish(Player vanishing) {
        if (!this.enabled) {
            return;
        }
        PlayerData data = this.playerData.get(vanishing.getName());
        if (data == null) {
            data = this.initPlayer(vanishing);
        }
        if (data.skin != null && data.cloak != null && data.title != null) {
            return;
        }
        for (final SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
            this.playerUpdate(vanishing, data, player);
        }
    }

    private void init() {
        this.playerData = new HashMap<String, PlayerData>();
        final File confFile = new File(this.plugin.getDataFolder(), "spoutcraft.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(confFile);
        config.options().copyDefaults(true);
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("spoutcraft.yml")));
        try {
            config.save(confFile);
        } catch (IOException e) {
            this.plugin.getServer().getLogger().log(Level.SEVERE, "Could not save spoutcraft.yml", e);
        }
        for (final String skinGroup : config.getConfigurationSection("skins").getKeys(false)) {
            this.skins.put(skinGroup, config.getString("skins." + skinGroup));
        }
        for (final String cloakGroup : config.getConfigurationSection("cloaks").getKeys(false)) {
            this.cloaks.put(cloakGroup, config.getString("cloaks." + cloakGroup));
        }
        for (final String titleGroup : config.getConfigurationSection("titles").getKeys(false)) {
            this.titles.put(titleGroup, config.getString("titles." + titleGroup).replace("%r", "\n").replace("&&", "§"));
        }
    }

    private PlayerData initPlayer(Player player) {
        String skin = null;
        String cloak = null;
        String title = null;
        for (final String skinGroup : new HashSet<String>(this.skins.keySet())) {
            if (player.hasPermission("vanish.skingroup." + skinGroup)) {
                skin = this.skins.get(skinGroup);
                break;
            }
        }
        for (final String cloakGroup : new HashSet<String>(this.cloaks.keySet())) {
            if (player.hasPermission("vanish.cloakgroup." + cloakGroup)) {
                cloak = this.cloaks.get(cloakGroup);
                break;
            }
        }
        for (final String titleGroup : new HashSet<String>(this.titles.keySet())) {
            if (player.hasPermission("vanish.titlegroup." + titleGroup)) {
                title = this.titles.get(titleGroup).replace("%n", player.getName());
                break;
            }
        }
        final PlayerData pData = new PlayerData(skin, cloak, title);
        this.playerData.put(player.getName(), pData);
        return pData;
    }

    /**
     * For player target, update spout vanishness with data.
     * 
     * @param vanished
     * @param data
     * @param target
     */
    private void playerUpdate(Player vanished, PlayerData data, SpoutPlayer target) {
        if ((target != null) && target.hasPermission("vanish.see") && target.isSpoutCraftEnabled()) {
            if (data.skin != null) {
                SpoutManager.getAppearanceManager().setPlayerSkin(target, vanished, data.skin);
            }
            if (data.cloak != null) {
                SpoutManager.getAppearanceManager().setPlayerCloak(target, vanished, data.cloak);
            }
            if (data.title != null) {
                SpoutManager.getAppearanceManager().setPlayerTitle(target, vanished, data.title);
            }
        }
    }

}
