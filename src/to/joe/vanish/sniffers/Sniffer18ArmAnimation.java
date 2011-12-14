package to.joe.vanish.sniffers;

import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer18ArmAnimation extends Sniffer {

    public Sniffer18ArmAnimation(VanishManager vanish) {
        super(net.minecraft.server.Packet18ArmAnimation.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet18ArmAnimation) ((MCCraftPacket) packet).getPacket()).a);
    }

}
