package to.joe.vanish.sniffers;

import net.minecraft.server.Packet17EntityLocationAction;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer17EntityLocationAction extends Sniffer {

    public Sniffer17EntityLocationAction(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) throws ClassCastException{
        return !this.vanish.shouldHide(player, ((Packet17EntityLocationAction) ((MCCraftPacket) packet).getPacket()).a);
    }

}
