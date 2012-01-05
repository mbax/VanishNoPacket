package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer39AttachEntity extends Sniffer {

    public Sniffer39AttachEntity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet39AttachEntity) packet).a);
    }

}
