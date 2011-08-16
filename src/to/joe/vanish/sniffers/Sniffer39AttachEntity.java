package to.joe.vanish.sniffers;

import net.minecraft.server.Packet39AttachEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer39AttachEntity implements PacketListener {

    private final VanishManager vanish;

    public Sniffer39AttachEntity(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet39AttachEntity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
