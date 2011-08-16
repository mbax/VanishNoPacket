package to.joe.vanish.sniffers;

import net.minecraft.server.Packet34EntityTeleport;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer34EntityTeleport implements PacketListener {

    private final VanishManager vanish;

    public Sniffer34EntityTeleport(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet34EntityTeleport) ((MCCraftPacket) packet).getPacket()).a);
    }

}
