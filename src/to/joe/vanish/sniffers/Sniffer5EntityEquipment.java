package to.joe.vanish.sniffers;

import net.minecraft.server.Packet5EntityEquipment;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer5EntityEquipment extends Sniffer {

    public Sniffer5EntityEquipment(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet5EntityEquipment) ((MCCraftPacket) packet).getPacket()).a);
    }

}
