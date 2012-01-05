package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet5EntityEquipment;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer5EntityEquipment extends Sniffer {

    public Sniffer5EntityEquipment(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet5EntityEquipment) packet).a);
    }

}
