package to.joe.vanish.users;

import org.bukkit.entity.Player;

public class VanishUser {
    private boolean seeAll;
    private boolean statusUpdates;
    private boolean noPickup;
    private boolean noFollow;
    private boolean preventIncomingDamage;
    private boolean preventOutgoingDamage;

    public VanishUser(Player player) {
        this.seeAll = player.hasPermission("vanish.see");
        this.statusUpdates = player.hasPermission("vanish.statusupdates");
        this.noPickup = player.hasPermission("vanish.nopickup");
        this.noFollow = player.hasPermission("vanish.nofollow");
        this.preventIncomingDamage = player.hasPermission("vanish.preventincomingdamage");
        this.preventOutgoingDamage = player.hasPermission("vanish.preventoutgoingdamage");
    }

    public boolean getNoFollow() {
        return this.noFollow;
    }

    public boolean getNoPickup() {
        return this.noPickup;
    }

    public boolean getPreventIncomingDamage() {
        return this.preventIncomingDamage;
    }

    public boolean getPreventOutgoingDamage() {
        return this.preventOutgoingDamage;
    }

    public boolean getSeeAll() {
        return this.seeAll;
    }

    public boolean getStatusUpdates() {
        return this.statusUpdates;
    }

    public boolean toggleIncomingDamage() {
        this.preventIncomingDamage = !this.preventIncomingDamage;
        return this.preventIncomingDamage;
    }

    public boolean toggleNoFollow() {
        this.noFollow = !this.noFollow;
        return this.noFollow;
    }

    public boolean toggleNoPickup() {
        this.noPickup = !this.noPickup;
        return this.noPickup;
    }

    public boolean toggleOutgoingDamage() {
        this.preventOutgoingDamage = !this.preventOutgoingDamage;
        return this.preventOutgoingDamage;
    }

    public boolean toggleSeeAll() {
        this.seeAll = !this.seeAll;
        return this.seeAll;
    }

    public boolean toggleStatusUpdates() {
        this.statusUpdates = !this.statusUpdates;
        return this.statusUpdates;
    }
}
