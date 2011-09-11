package to.joe.vanish;

import java.util.ArrayList;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import to.joe.vanish.sniffers.Sniffer17;
import to.joe.vanish.sniffers.Sniffer18ArmAnimation;
import to.joe.vanish.sniffers.Sniffer19EntityAction;
import to.joe.vanish.sniffers.Sniffer20NamedEntitySpawn;
import to.joe.vanish.sniffers.Sniffer28EntityVelocity;
import to.joe.vanish.sniffers.Sniffer30Entity;
import to.joe.vanish.sniffers.Sniffer31RelEntityMove;
import to.joe.vanish.sniffers.Sniffer32EntityLook;
import to.joe.vanish.sniffers.Sniffer33RelEntityMoveLook;
import to.joe.vanish.sniffers.Sniffer34EntityTeleport;
import to.joe.vanish.sniffers.Sniffer38EntityStatus;
import to.joe.vanish.sniffers.Sniffer39AttachEntity;
import to.joe.vanish.sniffers.Sniffer5EntityEquipment;

/**
 * It's the vanishing manager!
 * 
 * @author mbaxter
 * 
 */
public class VanishManager {

    private final VanishPlugin plugin;
    private final Object syncEID = new Object();

    private final Sniffer5EntityEquipment sniffer5 = new Sniffer5EntityEquipment(this);
    private final Sniffer17 sniffer17 = new Sniffer17(this);
    private final Sniffer18ArmAnimation sniffer18 = new Sniffer18ArmAnimation(this);
    private final Sniffer19EntityAction sniffer19 = new Sniffer19EntityAction(this);
    private final Sniffer20NamedEntitySpawn sniffer20 = new Sniffer20NamedEntitySpawn(this);
    private final Sniffer28EntityVelocity sniffer28 = new Sniffer28EntityVelocity(this);
    //private final Sniffer29DestroyEntity sniffer29 = new Sniffer29DestroyEntity(this);
    private final Sniffer30Entity sniffer30 = new Sniffer30Entity(this);
    private final Sniffer31RelEntityMove sniffer31 = new Sniffer31RelEntityMove(this);
    private final Sniffer32EntityLook sniffer32 = new Sniffer32EntityLook(this);
    private final Sniffer33RelEntityMoveLook sniffer33 = new Sniffer33RelEntityMoveLook(this);
    private final Sniffer34EntityTeleport sniffer34 = new Sniffer34EntityTeleport(this);
    private final Sniffer38EntityStatus sniffer38 = new Sniffer38EntityStatus(this);
    private final Sniffer39AttachEntity sniffer39 = new Sniffer39AttachEntity(this);

    private ArrayList<Integer> listOfEntityIDs;

    private VanishAnnounceManipulator manipulator;

    public VanishManager(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void disable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && VanishPerms.canVanish(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "[VANISH] Disabled. All users visible now.");
            }
        }
        SpoutManager.getPacketManager().removeListener(5, this.sniffer5);
        SpoutManager.getPacketManager().removeListener(17, this.sniffer17);
        SpoutManager.getPacketManager().removeListener(18, this.sniffer18);
        SpoutManager.getPacketManager().removeListener(19, this.sniffer19);
        SpoutManager.getPacketManager().removeListener(20, this.sniffer20);
        SpoutManager.getPacketManager().removeListener(28, this.sniffer28);
        // SpoutManager.getPacketManager().removeListener(29, sniffer29);
        SpoutManager.getPacketManager().removeListener(30, this.sniffer30);
        SpoutManager.getPacketManager().removeListener(31, this.sniffer31);
        SpoutManager.getPacketManager().removeListener(32, this.sniffer32);
        SpoutManager.getPacketManager().removeListener(33, this.sniffer33);
        SpoutManager.getPacketManager().removeListener(34, this.sniffer34);
        SpoutManager.getPacketManager().removeListener(38, this.sniffer38);
        SpoutManager.getPacketManager().removeListener(39, this.sniffer39);
        this.revealAll();
    }

    public VanishAnnounceManipulator getAnnounceManipulator() {
        return this.manipulator;
    }

    public VanishPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Is the player vanished?
     * 
     * @param player
     * @return true if vanished
     */
    public boolean isVanished(Player player) {
        synchronized (this.syncEID) {
            return this.listOfEntityIDs.contains(((CraftPlayer) player).getEntityId());
        }
    }

    public boolean isVanished(String playerName) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        if (player != null) {
            return this.isVanished(player);
        }
        return false;
    }

    public void packetSending(Player vanishingPlayer) {
        final boolean vanishing = !this.isVanished(vanishingPlayer);
        final String vanishingPlayerName = vanishingPlayer.getName();
        if (vanishing) {
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", true);
            this.addEIDVanished(((CraftPlayer) vanishingPlayer).getEntityId());
            this.plugin.log(vanishingPlayerName + " disappeared.");
        } else {
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", true);
            this.removeVanished(((CraftPlayer) vanishingPlayer).getEntityId());
            this.plugin.log(vanishingPlayerName + " reappeared.");
        }
        final Player[] playerList = this.plugin.getServer().getOnlinePlayers();
        for (final Player otherPlayer : playerList) {
            if ((this.getDistance(vanishingPlayer, otherPlayer) > 512) || (otherPlayer.equals(vanishingPlayer))) {
                continue;
            }
            if (vanishing) {
                this.destroyEntity(vanishingPlayer, otherPlayer);
                if (VanishPerms.canSeeAll(otherPlayer)) {
                    this.undestroyEntity(vanishingPlayer, otherPlayer);
                }
            } else {
                if (VanishPerms.canSeeAll(otherPlayer)) {
                    this.destroyEntity(vanishingPlayer, otherPlayer);
                }
                this.undestroyEntity(vanishingPlayer, otherPlayer);
            }
        }
    }

    /**
     * Drop a player from the list of vanished
     * Useful for when they quit
     * 
     * @param player
     */
    public void removeVanished(Player player) {
        this.removeVanished(((CraftPlayer) player).getEntityId());
    }

    public void resetSeeing(Player player) {
        if (VanishPerms.canSeeAll(player)) {
            this.showVanished(player);
        } else {
            this.hideVanished(player);
        }
    }

    /**
     * If the entity id eid should be hidden from the player
     * 
     * @param player
     * @param eid
     * @return if the eid is vanished and the player shouldn't see it
     */
    public boolean shouldHide(Player player, int eid) {
        if (!VanishPerms.canSeeAll(player)) {
            return this.isVanished(eid);
        }
        return false;
    }

    /**
     * Smack that vanish list. Smack it hard.
     */
    public void startup(String fakejoin, String fakequit, boolean delayedJoinTracking) {
        SpoutManager.getPacketManager().addListener(5, this.sniffer5);
        SpoutManager.getPacketManager().addListener(17, this.sniffer17);
        SpoutManager.getPacketManager().addListener(18, this.sniffer18);
        SpoutManager.getPacketManager().addListener(19, this.sniffer19);
        SpoutManager.getPacketManager().addListener(20, this.sniffer20);
        SpoutManager.getPacketManager().addListener(28, this.sniffer28);
        // SpoutManager.getPacketManager().addListener(29, sniffer29);
        SpoutManager.getPacketManager().addListener(30, this.sniffer30);
        SpoutManager.getPacketManager().addListener(31, this.sniffer31);
        SpoutManager.getPacketManager().addListener(32, this.sniffer32);
        SpoutManager.getPacketManager().addListener(33, this.sniffer33);
        SpoutManager.getPacketManager().addListener(34, this.sniffer34);
        SpoutManager.getPacketManager().addListener(38, this.sniffer38);
        SpoutManager.getPacketManager().addListener(39, this.sniffer39);
        this.manipulator = new VanishAnnounceManipulator(this.plugin, fakejoin, fakequit, delayedJoinTracking);
        this.listOfEntityIDs = new ArrayList<Integer>();
    }

    /**
     * Toggle a player's visibility
     * 
     * @param togglingPlayer
     *            The player disappearing
     */
    public void toggleVanish(Player togglingPlayer) {
        this.packetSending(togglingPlayer);
        final String vanishingPlayerName = togglingPlayer.getName();
        String messageVanisher;
        final String base = ChatColor.YELLOW + vanishingPlayerName + " has ";
        if (this.isVanished(togglingPlayer)) {
            this.plugin.hooksVanish(togglingPlayer);
            messageVanisher = base + "vanished. Poof.";

        } else {
            this.plugin.hooksUnvanish(togglingPlayer);
            messageVanisher = base + "become visible.";
            this.manipulator.toggled(togglingPlayer.getName());
        }
        this.plugin.messageUpdate(messageVanisher);
    }

    private void addEIDVanished(int id) {
        synchronized (this.syncEID) {
            this.listOfEntityIDs.add(id);
        }
    }

    private void destroyEntity(Player vanishingPlayer, Player obliviousPlayer) {
        ((CraftPlayer) obliviousPlayer).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(((CraftPlayer) vanishingPlayer).getEntityId()));
    }

    private double getDistance(Player player1, Player player2) {
        final Location loc1 = player1.getLocation();
        final Location loc2 = player1.getLocation();
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2.0D) + Math.pow(loc1.getY() - loc2.getY(), 2.0D) + Math.pow(loc1.getZ() - loc2.getZ(), 2.0D));
    }

    private void hideVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if ((this.getDistance(player, otherPlayer) > 512) || (otherPlayer.equals(player))) {
                continue;
            }
            if (this.isVanished(otherPlayer)) {
                this.destroyEntity(otherPlayer, player);
            }
        }
    }

    private boolean isVanished(int id) {
        synchronized (this.syncEID) {
            return this.listOfEntityIDs.contains(id);
        }
    }

    private void removeVanished(int id) {
        synchronized (this.syncEID) {
            this.listOfEntityIDs.remove(Integer.valueOf(id));
        }
    }

    private void revealAll() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            for (final Player player2 : this.plugin.getServer().getOnlinePlayers()) {
                if ((player != null) && (player2 != null) && !player.equals(player2)) {
                    this.destroyEntity(player, player2);
                    this.undestroyEntity(player, player2);
                }
            }
        }
    }

    private void showVanished(Player player) {
        for (final Player otherPlayer : this.plugin.getServer().getOnlinePlayers()) {
            if ((this.getDistance(player, otherPlayer) > 512) || (otherPlayer.equals(player))) {
                continue;
            }
            if (this.isVanished(otherPlayer)) {
                this.undestroyEntity(otherPlayer, player);
            }
        }
    }

    private void undestroyEntity(Player revealPlayer, Player nowAwarePlayer) {
        ((CraftPlayer) nowAwarePlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(((CraftPlayer) revealPlayer).getHandle()));
    }

}