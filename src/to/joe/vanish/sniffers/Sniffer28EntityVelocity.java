package to.joe.vanish.sniffers;

import net.minecraft.server.Packet28EntityVelocity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer28EntityVelocity extends Sniffer {

    public Sniffer28EntityVelocity(VanishManager vanish) {
        super(net.minecraft.server.Packet28EntityVelocity.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet28EntityVelocity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
