package org.kitteh.vanish.staticaccess;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

/**
 * Static party!
 * This is where you can grab stuff easily
 * 
 * @deprecated This class will disappear in VNP 4.0
 */
@Deprecated
public final class VanishNoPacket {
    private static VanishPlugin instance;
    private static Thread mainThread;

    /**
     * Queries if a player can see another player
     *
     * @param looking the player who might be able to see another
     * @param uncertain the player who may or may not be seen
     * @return true if the looking player can see the other
     * @throws VanishNotLoadedException is VNP isn't loaded
     * @deprecated Use the Bukkit API
     */
    @Deprecated
    public static boolean canSee(Player looking, Player uncertain) throws VanishNotLoadedException {
        VanishNoPacket.check();
        return !(VanishNoPacket.instance.getManager().isVanished(uncertain) && !VanishPerms.canSeeAll(looking));
    }

    /**
     * Gets the VanishManager
     * DO NOT STORE THE MANAGER.
     *
     * @return the VNP manager itself, if you like it that way
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Use the Bukkit API
     */
    @Deprecated
    public static VanishManager getManager() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager();
    }

    /**
     * Gets the plugin
     * DO NOT STORE THE PLUGIN.
     *
     * @return the VNP plugin itself, if you like it that way
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Use the Bukkit API
     */
    @Deprecated
    public static VanishPlugin getPlugin() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance;
    }

    /**
     * Gets if a player is vanished
     *
     * @param name player to check
     * @return true if named player is invisible
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Use the player metadata
     */
    @Deprecated
    public static boolean isVanished(String name) throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager().isVanished(name);
    }

    /**
     * Gets the number of users vanished
     *
     * @return count of vanished players on server
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Use player metadata and count.
     */
    @Deprecated
    public static int numVanished() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager().numVanished();
    }

    /**
     * If you aren't VanishNoPacket itself, you shouldn't be here.
     *
     * @param instance STOP IT WHAT ARE YOU DOING
     * @deprecated SERIOUSLY WHAT ARE YOU DOING
     */
    @Deprecated
    public static void setInstance(VanishPlugin instance) {
        VanishNoPacket.instance = instance;
        VanishNoPacket.mainThread = Thread.currentThread();
    }

    /**
     * Toggles a player's visibility quietly.
     *
     * @param player the vanishing player
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Call non-statically, getting the VanishManager via Bukkit
     */
    @Deprecated
    public static void toggleVanishSilent(Player player) throws VanishNotLoadedException {
        VanishNoPacket.check(false);
        VanishNoPacket.instance.getManager().toggleVanishQuiet(player);
    }

    /**
     * Toggles a player's visibility loudly.
     * Announces to player, and to those with appropriate perms
     *
     * @param player the vanishing player
     * @throws VanishNotLoadedException if VNP isn't loaded
     * @deprecated Call non-statically, getting the VanishManager via Bukkit
     */
    @Deprecated
    public static void toggleVanishWithAnnounce(Player player) throws VanishNotLoadedException {
        VanishNoPacket.check(false);
        VanishNoPacket.instance.getManager().toggleVanish(player);
    }

    private static void check() throws VanishNotLoadedException {
        VanishNoPacket.check(true);
    }

    private static void check(boolean safe) throws VanishNotLoadedException {
        if (VanishNoPacket.instance == null) {
            throw new VanishNotLoadedException();
        }
        if (!safe && !VanishNoPacket.mainThread.equals(Thread.currentThread())) {
            throw new RuntimeException("Cannot toggle visibility asynchronously");
        }
    }
}