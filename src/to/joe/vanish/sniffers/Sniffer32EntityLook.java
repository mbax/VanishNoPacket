package to.joe.vanish.sniffers;

import net.minecraft.server.Packet32EntityLook;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer32EntityLook extends Sniffer {

    public Sniffer32EntityLook(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet32EntityLook) ((MCCraftPacket) packet).getPacket()).a);
    }

}
