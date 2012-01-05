package org.kitteh.vanish.staticaccess;

import org.bukkit.entity.Player;

import to.joe.vanish.VanishManager;
import to.joe.vanish.VanishPlugin;

/**
 * Static party!
 * This is where you can grab stuff easily
 */
public class VanishNoPacket {

    private static VanishPlugin instance;

    /**
     * If you aren't VanishNoPacket itself, you shouldn't be here.
     * 
     * @param instance
     */
    public static void setInstance(VanishPlugin instance) {
        VanishNoPacket.instance = instance;
    }

    /**
     * DO NOT STORE THE MANAGER.
     * 
     * @return the VNP manager itself, if you like it that way
     * @throws VanishNotLoadedException
     */
    public static VanishManager getManager() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager();
    }

    /**
     * Vanish check
     * 
     * @param name
     *            player to check
     * @return true if named player is invisible
     * @throws VanishNotLoadedException
     */
    public static boolean isVanished(String name) throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager().isVanished(name);
    }

    /**
     * Vanish count
     * 
     * @return count of vanished players on server
     * @throws VanishNotLoadedException
     */
    public static int numVanished() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance.getManager().numVanished();
    }

    /**
     * Loud toggle. Announces to player, and to those with appropriate perms
     * 
     * @param player
     *            vanishing
     * @throws VanishNotLoadedException
     */
    public static void toggleVanishWithAnnounce(Player player) throws VanishNotLoadedException {
        VanishNoPacket.check();
        VanishNoPacket.instance.getManager().toggleVanish(player);
    }

    /**
     * Quiet toggle. Player goes poof!
     * 
     * @param player
     *            vanishing
     * @throws VanishNotLoadedException
     */
    public static void toggleVanishSilent(Player player) throws VanishNotLoadedException {
        VanishNoPacket.check();
        VanishNoPacket.instance.getManager().toggleVanishQuiet(player);
    }

    private static void check() throws VanishNotLoadedException {
        if (VanishNoPacket.instance == null) {
            throw new VanishNotLoadedException();
        }
    }
}
