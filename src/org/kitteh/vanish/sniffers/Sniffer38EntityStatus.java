package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet38EntityStatus;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer38EntityStatus extends Sniffer {

    public Sniffer38EntityStatus(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet38EntityStatus) ((MCCraftPacket) packet).getPacket()).a);
    }

}
