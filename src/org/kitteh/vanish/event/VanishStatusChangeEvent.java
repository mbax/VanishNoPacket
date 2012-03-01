package org.kitteh.vanish.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VanishStatusChangeEvent extends Event {

    public static HandlerList getHandlerList() {
        return VanishStatusChangeEvent.handlers;
    }

    private final String name;
    private final boolean vanishing;

    private static final HandlerList handlers = new HandlerList();

    public VanishStatusChangeEvent(String name, boolean vanishing) {
        this.name = name;
        this.vanishing = vanishing;
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
     * @return true if vanishing, false is revealing
     */
    public boolean isVanishing() {
        return this.vanishing;
    }

}
