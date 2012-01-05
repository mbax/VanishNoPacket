package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.kitteh.vanish.VanishManager;

public abstract class Sniffer implements PacketListener {

    protected VanishManager vanish;

    public Sniffer(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        try {
            return this.checkPakkit(player, ((MCCraftPacket) packet).getPacket());
        } catch (final ClassCastException e) {
            this.vanish.sanityCheck(e);
        }
        return true;
    }

    public abstract boolean checkPakkit(Player player, Packet packet) throws ClassCastException;

}
