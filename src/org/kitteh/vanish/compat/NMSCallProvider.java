package org.kitteh.vanish.compat;

import org.bukkit.entity.Player;

public interface NMSCallProvider {

    public void sendExplosionPacket(Player player, double x, double y, double z);

    public void sendEntityDestroy(Player player, int entityId);

    public void removeFromRemoveQueue(Player player, int entityId);

}
