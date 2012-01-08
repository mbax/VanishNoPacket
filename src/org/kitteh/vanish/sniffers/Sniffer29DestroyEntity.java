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
        return !this.vanish.shouldHide(player, ((Packet29DestroyEntity) packet).a);
    }

}
