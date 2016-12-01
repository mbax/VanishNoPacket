package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired whenever a player quit fakes
 */
public class VanishFakeQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return VanishFakeQuitEvent.handlers;
    }

    private final Player player;

    public VanishFakeQuitEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return VanishFakeQuitEvent.handlers;
    }

    /**
     * Gets the player quit fakes
     * 
     * @return the player quit fakes
     */
    public Player getPlayer() {
        return this.player;
    }
}
