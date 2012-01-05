package org.kitteh.vanish.listeners;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.kitteh.vanish.VanishPlugin;


public class ListenSpout extends SpoutListener {

    private final VanishPlugin plugin;

    public ListenSpout(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
        this.plugin.playerHasSpout(event.getPlayer());
    }
}
