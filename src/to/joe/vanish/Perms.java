package to.joe.vanish;

import org.bukkit.entity.Player;

/**
 * This is an utterly boring class
 */
public class Perms {
    public static boolean canNotPickUp(Player player) {
        return player.hasPermission("vanish.nopickup");
    }

    public static boolean canSeeAll(Player player) {
        return player.hasPermission("vanish.see");
    }

    public static boolean canVanish(Player player) {
        return player.hasPermission("vanish.vanish");
    }

    public static boolean canNotFollow(Player player) {
        return player.hasPermission("vanish.nofollow");
    }

    public static boolean blockOutgoingDamage(Player player) {
        return player.hasPermission("vanish.preventoutgoingdamage");
    }

    public static boolean blockIncomingDamage(Player player) {
        return player.hasPermission("vanish.preventincomingdamage");
    }
}
