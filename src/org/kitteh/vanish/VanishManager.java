package org.kitteh.vanish;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet201PlayerInfo;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.kitteh.vanish.sniffers.*;

/**
 * It's the vanishing manager!
 * 
 * @author mbaxter
 * 
 */
public class VanishManager {

    private final VanishPlugin plugin;

    private final Sniffer5EntityEquipment sniffer5 = new Sniffer5EntityEquipment(this);
    private final Sniffer17EntityLocationAction sniffer17 = new Sniffer17EntityLocationAction(this);
    private final Sniffer18ArmAnimation sniffer18 = new Sniffer18ArmAnimation(this);
    private final Sniffer19EntityAction sniffer19 = new Sniffer19EntityAction(this);
    private final Sniffer20NamedEntitySpawn sniffer20 = new Sniffer20NamedEntitySpawn(this);
    private final Sniffer28EntityVelocity sniffer28 = new Sniffer28EntityVelocity(this);
    private final Sniffer29DestroyEntity sniffer29 = new Sniffer29DestroyEntity(this);
    private final Sniffer30Entity sniffer30 = new Sniffer30Entity(this);
    private final Sniffer31RelEntityMove sniffer31 = new Sniffer31RelEntityMove(this);
    private final Sniffer32EntityLook sniffer32 = new Sniffer32EntityLook(this);
    private final Sniffer33RelEntityMoveLook sniffer33 = new Sniffer33RelEntityMoveLook(this);
    private final Sniffer34EntityTeleport sniffer34 = new Sniffer34EntityTeleport(this);
    private final Sniffer38EntityStatus sniffer38 = new Sniffer38EntityStatus(this);
    private final Sniffer39AttachEntity sniffer39 = new Sniffer39AttachEntity(this);
    private final Sniffer40EntityMetadata sniffer40 = new Sniffer40EntityMetadata(this);
    private final Sniffer41MobEffect sniffer41 = new Sniffer41MobEffect(this);
    private final Sniffer42RemoveMobEffect sniffer42 = new Sniffer42RemoveMobEffect(this);
    private final Sniffer201PlayerInfo sniffer201 = new Sniffer201PlayerInfo(this);

    private ArrayList<Integer> listOfVanishedEntityIDs;
    private ArrayList<String> listOfVanishedPlayerNames;
    private HashMap<Integer, Integer> safeListPacket29;
    private HashMap<String, Boolean> sleepIgnored;
    private HashMap<String, Integer> safeListPacket201;

    private Field packetQueueListField;

    private VanishAnnounceManipulator announceManipulator;

    private boolean tabControlEnabled;

    public VanishManager(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the Announce Manipulator
     */
    public VanishAnnounceManipulator getAnnounceManipulator() {
        return this.announceManipulator;
    }

    /**
     * @return Daddy!
     */
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
        return this.listOfVanishedPlayerNames.contains(player.getName());
    }

    /**
     * @param playerName
     * @return if the named player is currently vanished
     */
    public boolean isVanished(String playerName) {
        final Player player = this.plugin.getServer().getPlayer(playerName);
        if (player != null) {
            Debuggle.log("Testing vanished status of " + player.getName() + ": " + this.isVanished(player));
            return this.isVanished(player);
        }
        Debuggle.log("Testing vanished status of " + playerName + ": null");
        return false;
    }

    /**
     * @return the number of players currently vanished
     */
    public int numVanished() {
        return this.listOfVanishedEntityIDs.size();
    }

    /**
     * Only call this when disabling the plugin
     */
    public void onPluginDisable() {
        SpoutManager.getPacketManager().removeListener(5, this.sniffer5);
        SpoutManager.getPacketManager().removeListener(17, this.sniffer17);
        SpoutManager.getPacketManager().removeListener(18, this.sniffer18);
        SpoutManager.getPacketManager().removeListener(19, this.sniffer19);
        SpoutManager.getPacketManager().removeListener(20, this.sniffer20);
        SpoutManager.getPacketManager().removeListener(28, this.sniffer28);
        SpoutManager.getPacketManager().removeListener(29, this.sniffer29);
        SpoutManager.getPacketManager().removeListener(30, this.sniffer30);
        SpoutManager.getPacketManager().removeListener(31, this.sniffer31);
        SpoutManager.getPacketManager().removeListener(32, this.sniffer32);
        SpoutManager.getPacketManager().removeListener(33, this.sniffer33);
        SpoutManager.getPacketManager().removeListener(34, this.sniffer34);
        SpoutManager.getPacketManager().removeListener(38, this.sniffer38);
        SpoutManager.getPacketManager().removeListener(39, this.sniffer39);
        SpoutManager.getPacketManager().removeListener(40, this.sniffer40);
        SpoutManager.getPacketManager().removeListener(41, this.sniffer41);
        SpoutManager.getPacketManager().removeListener(42, this.sniffer42);
        SpoutManager.getPacketManager().removeListener(201, this.sniffer201);
        this.revealAll();
    }

    public void playerQuit(Player player) {
        Debuggle.log("Quitting: " + player.getName());
        VanishPerms.userQuit(player);
        this.removeVanished(player.getName(), ((CraftPlayer) player).getEntityId());
        this.plugin.getManager().safelist29Mod(((CraftPlayer) player).getEntityId(), this.plugin.getServer().getOnlinePlayers().length);
    }

    /**
     * Force a refresh of who a player can or can't see.
     * 
     * @param player
     */
    public void resetSeeing(Player player) {
        Debuggle.log("Resetting visibility on " + player.getName());
        if (VanishPerms.canSeeAll(player)) {
            this.showVanished(player);
        } else {
            this.hideVanished(player);
        }
    }

    public void sanityCheck(Exception e) {
        this.plugin.getServer().getLogger().log(Level.SEVERE, "[VANISH] A flaw has been caught. Please report this (it wont break things) to http://github.com/mbax/VanishNoPacket :) . ", e);
    }

    /**
     * If the entity id eid should be hidden from the player
     * This is called for all packets but 29
     * See shouldHide(Player player, int eid, boolean is29)
     * 
     * @param player
     * @param eid
     * @return if the eid is vanished and the player shouldn't see it
     */
    public boolean shouldHide(Player player, int eid) {
        return this.shouldHide(player, eid, false);
    }

    /**
     * If the entity id eid should be hidden from the player
     * If it's packet 29, handle the safelist.
     * 
     * @param player
     * @param eid
     * @param is29
     * @return
     */
    public boolean shouldHide(Player player, int eid, boolean is29) {
        if (is29 && this.onSafeList29(eid)) {
            this.safelist29Mod(eid, -1);
            return false;
        }
        if (!VanishPerms.canSeeAll(player)) {
            return this.isVanished(eid);
        }
        return false;
    }

    /**
     * If the named player should be hidden from the Player player
     * Called for Packet 201, the tab menu packet
     * 
     * @param player
     * @param name
     * @param status
     * @return
     */
    public boolean shouldHide(Player player, String tablistname, boolean status) {
        String name = "";
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if ((plr != null) && plr.getPlayerListName().equals(tablistname)) {
                name = plr.getName();
            }
        }
        if (!status && this.onSafeList201(name)) {
            this.safelist201Mod(name, -1);
            return false;
        }
        if (!VanishPerms.canSeeAll(player)) {
            return this.isVanished(name);
        }
        return false;
    }

    /**
     * Smack that vanish list. Smack it hard.
     * But really, don't call this.
     */
    public void startup(boolean tabControl) {
        SpoutManager.getPacketManager().addListener(5, this.sniffer5);
        SpoutManager.getPacketManager().addListener(17, this.sniffer17);
        SpoutManager.getPacketManager().addListener(18, this.sniffer18);
        SpoutManager.getPacketManager().addListener(19, this.sniffer19);
        SpoutManager.getPacketManager().addListener(20, this.sniffer20);
        SpoutManager.getPacketManager().addListener(28, this.sniffer28);
        SpoutManager.getPacketManager().addListener(29, this.sniffer29);
        SpoutManager.getPacketManager().addListener(30, this.sniffer30);
        SpoutManager.getPacketManager().addListener(31, this.sniffer31);
        SpoutManager.getPacketManager().addListener(32, this.sniffer32);
        SpoutManager.getPacketManager().addListener(33, this.sniffer33);
        SpoutManager.getPacketManager().addListener(34, this.sniffer34);
        SpoutManager.getPacketManager().addListener(38, this.sniffer38);
        SpoutManager.getPacketManager().addListener(39, this.sniffer39);
        SpoutManager.getPacketManager().addListener(40, this.sniffer40);
        SpoutManager.getPacketManager().addListener(41, this.sniffer41);
        SpoutManager.getPacketManager().addListener(42, this.sniffer42);
        SpoutManager.getPacketManager().addListener(201, this.sniffer201);
        this.announceManipulator = new VanishAnnounceManipulator(this.plugin);
        this.listOfVanishedEntityIDs = new ArrayList<Integer>();
        this.listOfVanishedPlayerNames = new ArrayList<String>();
        this.safeListPacket29 = new HashMap<Integer, Integer>();
        this.safeListPacket201 = new HashMap<String, Integer>();
        this.sleepIgnored = new HashMap<String, Boolean>();
        this.tabControlEnabled = tabControl;

        try {
            this.packetQueueListField = NetworkManager.class.getDeclaredField("highPriorityQueue");
        } catch (final Exception e) {
            this.plugin.getServer().getLogger().log(Level.SEVERE, "[Vanish] Could not hook into the system. Check for a VanishNoPacket update", e);
            this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
        }
        this.packetQueueListField.setAccessible(true);
    }

    /**
     * Toggle a player's visibility
     * Called when a player calls /vanish
     * Talks to the player and everyone with vanish.see
     * 
     * @param togglingPlayer
     *            The player disappearing
     */
    public void toggleVanish(Player togglingPlayer) {
        this.toggleVanishQuiet(togglingPlayer);
        final String vanishingPlayerName = togglingPlayer.getName();
        String messageBit;
        final String base = ChatColor.YELLOW + vanishingPlayerName + " has ";
        if (this.isVanished(togglingPlayer)) {
            Debuggle.log("LoudVanishToggle Vanishing " + togglingPlayer.getName());
            this.plugin.hooksVanish(togglingPlayer);
            messageBit = "vanished. Poof.";

        } else {
            Debuggle.log("LoudVanishToggle Revealing " + togglingPlayer.getName());
            this.plugin.hooksUnvanish(togglingPlayer);
            messageBit = "become visible.";
            this.announceManipulator.vanishToggled(togglingPlayer);
        }
        final String message = base + messageBit;
        togglingPlayer.sendMessage(ChatColor.DARK_AQUA + "You have " + messageBit);
        this.plugin.messageStatusUpdate(message, togglingPlayer);
    }

    /**
     * Handle vanishing or unvanishing for a player
     * Does not say anything.
     * Called by toggleVanish(Player)
     * 
     * @param vanishingPlayer
     */
    public void toggleVanishQuiet(Player vanishingPlayer) {
        final boolean vanishing = !this.isVanished(vanishingPlayer);
        final String vanishingPlayerName = vanishingPlayer.getName();
        if (vanishing) {
            Debuggle.log("It's invisible time! " + vanishingPlayer.getName());
            this.sleepIgnored.put(vanishingPlayerName, vanishingPlayer.isSleepingIgnored());
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", true);
            this.addVanished(vanishingPlayerName, ((CraftPlayer) vanishingPlayer).getEntityId());
            this.plugin.log(vanishingPlayerName + " disappeared.");
        } else {
            Debuggle.log("It's visible time! " + vanishingPlayer.getName());
            vanishingPlayer.setSleepingIgnored(this.sleepIgnored.remove(vanishingPlayerName));
            vanishingPlayer.addAttachment(this.plugin, "vanish.currentlyVanished", false);
            this.removeVanished(vanishingPlayerName, ((CraftPlayer) vanishingPlayer).getEntityId());
            this.plugin.log(vanishingPlayerName + " reappeared.");
        }
        final Player[] playerList = this.plugin.getServer().getOnlinePlayers();
        for (final Player otherPlayer : playerList) {
            if (vanishingPlayer.equals(otherPlayer)) {
                continue;
            }
            Debuggle.log("Determining what to do about " + vanishingPlayer.getName() + " for " + otherPlayer.getName());
            if ((this.getDistance(vanishingPlayer, otherPlayer) > 512) || (otherPlayer.equals(vanishingPlayer))) {
                Debuggle.log("Too far. Doing nothing about " + vanishingPlayer.getName() + " for " + otherPlayer.getName());
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

    private void addVanished(String name, int id) {
        this.listOfVanishedEntityIDs.add(id);
        this.listOfVanishedPlayerNames.add(name);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void destroyEntity(Player vanishingPlayer, Player obliviousPlayer) {
        Debuggle.log("Hiding " + vanishingPlayer.getName() + " from " + obliviousPlayer.getName() + " via packets");
        final CraftPlayer craftPlayer = ((CraftPlayer) obliviousPlayer);
        final int eid = craftPlayer.getEntityId();
        this.safelist29Mod(eid, 1);
        try {
            ((List) this.packetQueueListField.get(craftPlayer.getHandle().netServerHandler.networkManager)).add(0, new Packet29DestroyEntity(((CraftPlayer) vanishingPlayer).getEntityId()));
        } catch (final Exception e) {
            this.plugin.getServer().getLogger().log(Level.SEVERE, "[Vanish] Encountered a serious error", e);
        }
        if (this.tabControlEnabled) {
            craftPlayer.getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(vanishingPlayer.getPlayerListName(), false, 0));
            this.safelist201Mod(vanishingPlayer.getName(), 1);
        }
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
        return this.listOfVanishedEntityIDs.contains(id);
    }

    private boolean onSafeList201(String name) {
        return this.safeListPacket201.containsKey(name);
    }

    private boolean onSafeList29(Integer eid) {
        return this.safeListPacket29.containsKey(eid);
    }

    private void removeVanished(String name, int id) {
        this.listOfVanishedEntityIDs.remove(Integer.valueOf(id));
        this.listOfVanishedPlayerNames.remove(name);
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

    private void safelist201Mod(String name, Integer diff) {
        if (this.safeListPacket201.containsKey(name)) {
            diff += this.safeListPacket201.get(name);
        }
        if (diff == 0) {
            this.safeListPacket201.remove(name);
        } else {
            this.safeListPacket201.put(name, diff);
        }
    }

    private void safelist29Mod(Integer eid, Integer diff) {
        if (this.safeListPacket29.containsKey(eid)) {
            diff += this.safeListPacket29.get(eid);
        }
        if (diff == 0) {
            this.safeListPacket29.remove(eid);
        } else {
            this.safeListPacket29.put(eid, diff);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void undestroyEntity(Player revealPlayer, Player nowAwarePlayer) {
        Debuggle.log("Revealing " + revealPlayer.getName() + " from " + nowAwarePlayer.getName() + " via packets");
        final List packetQueue;
        try {
            packetQueue = ((List) this.packetQueueListField.get(((CraftPlayer) nowAwarePlayer).getHandle().netServerHandler.networkManager));
        } catch (final Exception e) {
            this.plugin.getServer().getLogger().log(Level.SEVERE, "[Vanish] Encountered a serious error", e);
            return;
        }
        final Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn(((CraftPlayer) revealPlayer).getHandle());
        if (Settings.enableColoration() && this.isVanished(revealPlayer)) {
            packet.b = ChatColor.DARK_AQUA + revealPlayer.getName();
            if (packet.b.length() > 15) {
                packet.b = packet.b.substring(0, 15);
            }
        }
        int where = 0;
        if (packetQueue.size() > 0) {
            where = 1;
        }
        try {
            packetQueue.add(where, packet);
        } catch (final Exception e) {
            packetQueue.add(0, packet);
        }
        if (this.tabControlEnabled) {
            ((CraftPlayer) nowAwarePlayer).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(revealPlayer.getPlayerListName(), true, 1));
        }
    }

}