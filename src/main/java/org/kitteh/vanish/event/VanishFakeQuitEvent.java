package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired whenever a player fake quits
 */
public final class VanishFakeQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return VanishFakeQuitEvent.handlers;
    }
    
    private final String message;
    private final Player player;

    public VanishFakeQuitEvent(Player player, String message) {        
        this.player = player;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return VanishFakeQuitEvent.handlers;
    }

    /**
     * Gets the name of the player fake quitting
     *
     * @return name of the user fake quitting
     */
    public String getName() {
        return this.player.getName();
    }

    /**
     * Gets the player changing fake quitting
     *
     * @return the player changing fake quitting
     */
    public Player getPlayer() {
        return this.player;
    }
    
     /**
     * Gets the fake quit message
     *
     * @return fake quit message
     */
    public String getQuitMessage() {
        return this.message;
    }

}