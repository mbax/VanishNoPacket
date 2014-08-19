package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired whenever a player join fakes
 */
public class VanishFakeJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return VanishFakeJoinEvent.handlers;
    }

    private final Player player;

    public VanishFakeJoinEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return VanishFakeJoinEvent.handlers;
    }

    /**
     * Gets the player join fakes
     * 
     * @return the player join fakes
     */
    public Player getPlayer() {
        return this.player;
    }
}
