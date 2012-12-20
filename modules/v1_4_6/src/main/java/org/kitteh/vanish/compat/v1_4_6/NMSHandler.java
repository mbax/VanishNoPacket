package org.kitteh.vanish.compat.v1_4_6;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.kitteh.vanish.compat.api.NMSCallProvider;

import net.minecraft.server.v1_4_6.Block;
import net.minecraft.server.v1_4_6.Packet29DestroyEntity;
import net.minecraft.server.v1_4_6.Packet60Explosion;

public class NMSHandler implements NMSCallProvider {

    @Override
    public void sendExplosionPacket(Player player, double x, double y, double z) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new Packet60Explosion(x, y, z, 3, new ArrayList<Block>(), null));
    }

    @Override
    public void sendEntityDestroy(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new Packet29DestroyEntity(entityId));
    }

    @Override
    public void removeFromRemoveQueue(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().removeQueue.remove(Integer.valueOf(entityId));
    }

}
