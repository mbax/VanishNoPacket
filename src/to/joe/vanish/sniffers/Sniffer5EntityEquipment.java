package to.joe.vanish.sniffers;

import net.minecraft.server.Packet5EntityEquipment;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer5EntityEquipment implements PacketListener {

    private final VanishManager vanish;

    public Sniffer5EntityEquipment(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet5EntityEquipment) ((MCCraftPacket) packet).getPacket()).a);
    }

}
