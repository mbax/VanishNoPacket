package org.kitteh.vanish.compat.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NMSCallProvider {

    public void sendExplosionPacket(Location loc);

    public void sendEntityDestroy(Player player, int entityId);

}
