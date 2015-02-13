package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired whenever a player fake joins
 */
public final class VanishFakeJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return VanishFakeJoinEvent.handlers;
    }
    
    private final String message;
    private final Player player;

    public VanishFakeJoinEvent(Player player, String message) {        
        this.player = player;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return VanishFakeJoinEvent.handlers;
    }

    /**
     * Gets the name of the player fake joining
     *
     * @return name of the user fake joining
     */
    public String getName() {
        return this.player.getName();
    }

    /**
     * Gets the player changing fake joining
     *
     * @return the player changing fake joining
     */
    public Player getPlayer() {
        return this.player;
    }
    
     /**
     * Gets the fake join message
     *
     * @return fake join message
     */
    public String getJoinMessage() {
        return this.message;
    }

}