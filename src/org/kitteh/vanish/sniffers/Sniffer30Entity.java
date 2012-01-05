package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet30Entity;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer30Entity extends Sniffer {

    public Sniffer30Entity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet30Entity) packet).a);
    }

}
