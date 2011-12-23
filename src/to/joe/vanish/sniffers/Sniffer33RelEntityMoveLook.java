package to.joe.vanish.sniffers;

import net.minecraft.server.Packet33RelEntityMoveLook;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer33RelEntityMoveLook extends Sniffer {

    public Sniffer33RelEntityMoveLook(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet33RelEntityMoveLook) ((MCCraftPacket) packet).getPacket()).a);
    }

}
