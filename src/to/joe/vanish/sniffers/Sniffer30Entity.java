package to.joe.vanish.sniffers;

import net.minecraft.server.Packet30Entity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer30Entity extends Sniffer {

    public Sniffer30Entity(VanishManager vanish) {
        super(net.minecraft.server.Packet30Entity.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet30Entity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
