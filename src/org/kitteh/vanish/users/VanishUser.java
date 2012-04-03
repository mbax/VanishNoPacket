package org.kitteh.vanish.users;

import org.bukkit.entity.Player;

public class VanishUser {
    private boolean seeAll;
    private boolean noPickup;
    private boolean noFollow;
    private boolean preventIncomingDamage;
    private boolean preventOutgoingDamage;
    private boolean noInteract;
    private boolean noChat;
    private boolean silenChestReads;

    public VanishUser(Player player) {
        this.seeAll = player.hasPermission("vanish.see");
        this.noPickup = player.hasPermission("vanish.nopickup");
        this.noFollow = player.hasPermission("vanish.nofollow");
        this.preventIncomingDamage = player.hasPermission("vanish.preventincomingdamage");
        this.preventOutgoingDamage = player.hasPermission("vanish.preventoutgoingdamage");
        this.noInteract = player.hasPermission("vanish.nointeract");
        this.noChat = player.hasPermission("vanish.nochat");
        this.silenChestReads = player.hasPermission("vanish.readchests");
    }

    public boolean getNoChat() {
        return this.noChat;
    }

    public boolean getNoFollow() {
        return this.noFollow;
    }

    public boolean getNoInteract() {
        return this.noInteract;
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
    
    public boolean getSilentChestReads() {
        return this.silenChestReads;
    }
    
    public boolean toggleSilentChestReads() {
        this.silenChestReads = !this.silenChestReads;
        return this.silenChestReads;
    }

    public boolean toggleIncomingDamage() {
        this.preventIncomingDamage = !this.preventIncomingDamage;
        return this.preventIncomingDamage;
    }

    public boolean toggleNoChat() {
        this.noChat = !this.noChat;
        return this.noChat;
    }

    public boolean toggleNoFollow() {
        this.noFollow = !this.noFollow;
        return this.noFollow;
    }

    public boolean toggleNoInteract() {
        this.noInteract = !this.noInteract;
        return this.noInteract;
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

}