package org.kitteh.vanish.sneak;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.Vec3D;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public class SneakMonitor implements Listener {

    public SneakMonitor(final VanishPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (final World world : plugin.getServer().getWorlds()) {
                    final net.minecraft.server.World nmsWorld = ((CraftWorld) world).getHandle();
                    for (final Player player1 : world.getPlayers()) {

                        final boolean p1vanished = plugin.getManager().isVanished(player1);
                        final EntityLiving ent1 = ((CraftPlayer) player1).getHandle();
                        final Location loc1 = player1.getLocation();
                        final Vec3D vec3d1 = Vec3D.create(ent1.locX, ent1.locY + ent1.getHeadHeight(), ent1.locZ);
                        for (final Player player2 : world.getPlayers()) {
                            if (p1vanished && !VanishPerms.canSeeAll(player2)) {
                                continue;
                            }
                            boolean hide = false;
                            final Location loc2 = player2.getLocation();
                            if (loc1.distance(loc2) > 30) {//TODO config
                                hide = true;
                            } else if (player1.isSneaking()) {
                                final EntityLiving ent2 = ((CraftPlayer) player2).getHandle();
                                final Vec3D vec3d2 = Vec3D.create(ent2.locX, ent2.locY + ent2.getHeadHeight(), ent2.locZ);
                                if (nmsWorld.rayTrace(vec3d2, vec3d1, false) != null) {
                                    hide = true;
                                }
                            }
                            if (hide) {
                                if (player2.canSee(player1)) {
                                    player2.hidePlayer(player1);
                                }
                            } else {
                                if (!player2.canSee(player1)) {
                                    player2.showPlayer(player1);
                                }
                            }
                        }
                    }
                }
            }
        }, 2, 2);
    }

}
