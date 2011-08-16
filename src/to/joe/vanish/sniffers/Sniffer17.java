package to.joe.vanish.sniffers;

import net.minecraft.server.Packet17;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer17 implements PacketListener {

    private final VanishManager vanish;

    public Sniffer17(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet17) ((MCCraftPacket) packet).getPacket()).a);
    }

}
