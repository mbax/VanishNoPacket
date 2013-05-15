package org.kitteh.vanish.staticaccess;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

/**
 * Static party!
 * This is where you can grab stuff easily
 */
public final class VanishNoPacket {
    private static VanishPlugin instance;
    private static Thread mainThread;

    /**
     * Query if player looking can see player uncertain
     * 
     * @param looking
     * @param uncertain
     * @return true if can see
     * @throws VanishNotLoadedException
     */
    public static boolean canSee(Player looking, Player uncertain) throws VanishNotLoadedException {
        VanishNoPacket.check();
        return !(VanishNoPacket.instance.getManager().isVanished(uncertain) && !VanishPerms.canSeeAll(looking));
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
     * DO NOT STORE THE PLUGIN.
     * 
     * @return the VNP plugin itself, if you like it that way
     * @throws VanishNotLoadedException
     */
    public static VanishPlugin getPlugin() throws VanishNotLoadedException {
        VanishNoPacket.check();
        return VanishNoPacket.instance;
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
     * If you aren't VanishNoPacket itself, you shouldn't be here.
     * 
     * @param instance
     */
    public static void setInstance(VanishPlugin instance) {
        VanishNoPacket.instance = instance;
        VanishNoPacket.mainThread = Thread.currentThread();
    }

    /**
     * Quiet toggle. Player goes poof!
     * 
     * @param player
     *            vanishing
     * @throws VanishNotLoadedException
     */
    public static void toggleVanishSilent(Player player) throws VanishNotLoadedException {
        VanishNoPacket.check(false);
        VanishNoPacket.instance.getManager().toggleVanishQuiet(player);
    }

    /**
     * Loud toggle. Announces to player, and to those with appropriate perms
     * 
     * @param player
     *            vanishing
     * @throws VanishNotLoadedException
     */
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