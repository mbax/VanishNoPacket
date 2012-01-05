package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet28EntityVelocity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer28EntityVelocity extends Sniffer {

    public Sniffer28EntityVelocity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet28EntityVelocity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
