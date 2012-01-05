package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;


public class Sniffer40EntityMetadata extends Sniffer {

    public Sniffer40EntityMetadata(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet40EntityMetadata) ((MCCraftPacket) packet).getPacket()).a);
    }

}
