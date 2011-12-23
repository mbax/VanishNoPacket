package to.joe.vanish.sniffers;

import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer39AttachEntity extends Sniffer {

    public Sniffer39AttachEntity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet39AttachEntity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
