package org.kitteh.vanish.compat.pre;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.kitteh.vanish.compat.NMSCallProvider;

import net.minecraft.server.Block;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet60Explosion;

public class NMSHandler implements NMSCallProvider {

    @Override
    public void sendExplosionPacket(Player player, double x, double y, double z) {
        ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet60Explosion(x, y, z, 3, new ArrayList<Block>(), null));
    }

    @Override
    public void sendEntityDestroy(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(entityId));
    }

    @Override
    public void removeFromRemoveQueue(Player player, int entityId) {
        ((CraftPlayer) player).getHandle().removeQueue.remove(Integer.valueOf(entityId));
    }

}
