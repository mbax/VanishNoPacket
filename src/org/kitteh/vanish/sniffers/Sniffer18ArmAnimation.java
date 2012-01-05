package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer18ArmAnimation extends Sniffer {

    public Sniffer18ArmAnimation(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet18ArmAnimation) ((MCCraftPacket) packet).getPacket()).a);
    }

}
