package org.kitteh.vanish.listeners;

import java.lang.reflect.Field;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.kitteh.vanish.VanishPlugin;

public class ListenServer implements Listener {

    private final VanishPlugin plugin;
    private Field count;

    public ListenServer(VanishPlugin plugin) {
        this.plugin = plugin;
        try {
            this.count = ServerListPingEvent.class.getDeclaredField("numPlayers");
            this.count.setAccessible(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        try {
            this.count.setInt(event, (this.count.getInt(event) - this.plugin.getManager().numVanished()));
        } catch (final Exception e) {
            //Panic
        }
    }
}
