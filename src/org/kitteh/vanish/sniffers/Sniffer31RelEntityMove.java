package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet31RelEntityMove;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer31RelEntityMove extends Sniffer {

    public Sniffer31RelEntityMove(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet31RelEntityMove) ((MCCraftPacket) packet).getPacket()).a);
    }

}
