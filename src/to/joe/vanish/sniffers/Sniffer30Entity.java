package to.joe.vanish.sniffers;

import net.minecraft.server.Packet30Entity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer30Entity extends Sniffer {

    public Sniffer30Entity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet30Entity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
