package to.joe.vanish.sniffers;

import net.minecraft.server.Packet31RelEntityMove;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer31RelEntityMove extends Sniffer {

    public Sniffer31RelEntityMove(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet31RelEntityMove) ((MCCraftPacket) packet).getPacket()).a);
    }

}
