package to.joe.vanish.sniffers;

import net.minecraft.server.Packet19EntityAction;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer19EntityAction extends Sniffer {

    public Sniffer19EntityAction(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet19EntityAction) ((MCCraftPacket) packet).getPacket()).a);
    }

}
