package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event fired whenever a player changes their visibility
 */
public final class VanishStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return VanishStatusChangeEvent.handlers;
    }

    private final String name;
    private final boolean vanishing;
    private final Player player;

    public VanishStatusChangeEvent(Player player, boolean vanishing) {
        this.name = player.getName();
        this.vanishing = vanishing;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return VanishStatusChangeEvent.handlers;
    }

    /**
     * Gets the name of the player changing visibility
     *
     * @return name of the user changing visibility
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the player changing visibility
     *
     * @return the player changing visibility
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets if this is a vanish or unvanish
     *
     * @return true if vanishing, false is revealing
     */
    public boolean isVanishing() {
        return this.vanishing;
    }
}