package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet32EntityLook;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer32EntityLook extends Sniffer {

    public Sniffer32EntityLook(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet32EntityLook) packet).a);
    }

}
