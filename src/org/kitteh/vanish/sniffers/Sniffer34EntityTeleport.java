package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet34EntityTeleport;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer34EntityTeleport extends Sniffer {

    public Sniffer34EntityTeleport(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet34EntityTeleport) ((MCCraftPacket) packet).getPacket()).a);
    }

}
