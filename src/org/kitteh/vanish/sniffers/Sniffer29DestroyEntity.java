package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer29DestroyEntity extends Sniffer {

    public Sniffer29DestroyEntity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        final Packet29DestroyEntity packit = ((Packet29DestroyEntity) packet);
        if (packit instanceof VanishManager.Hat) {
            return true;
        }
        return !this.vanish.shouldHide(player, packit.a);
    }

}
