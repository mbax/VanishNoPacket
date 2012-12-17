package org.kitteh.vanish.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VanishStatusChangeEvent extends Event {

    public static HandlerList getHandlerList() {
        return VanishStatusChangeEvent.handlers;
    }

    private final String name;
    private final boolean vanishing;
    private final Player player;

    private static final HandlerList handlers = new HandlerList();

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
     * @return name of the user changing visibility
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the player changing visibility
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return true if vanishing, false is revealing
     */
    public boolean isVanishing() {
        return this.vanishing;
    }

}
