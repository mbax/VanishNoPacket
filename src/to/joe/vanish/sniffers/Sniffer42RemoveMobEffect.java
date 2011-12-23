package to.joe.vanish.sniffers;

import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer42RemoveMobEffect extends Sniffer {

    public Sniffer42RemoveMobEffect(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet42RemoveMobEffect) ((MCCraftPacket) packet).getPacket()).a);
    }

}
