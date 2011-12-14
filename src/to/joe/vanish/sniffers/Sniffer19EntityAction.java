package to.joe.vanish.sniffers;

import net.minecraft.server.Packet19EntityAction;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer19EntityAction extends Sniffer {

    public Sniffer19EntityAction(VanishManager vanish) {
        super(net.minecraft.server.Packet19EntityAction.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet19EntityAction) ((MCCraftPacket) packet).getPacket()).a);
    }

}
