package to.joe.vanish.sniffers;

import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer40EntityMetadata extends Sniffer {

    public Sniffer40EntityMetadata(VanishManager vanish) {
        super(net.minecraft.server.Packet40EntityMetadata.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet40EntityMetadata) ((MCCraftPacket) packet).getPacket()).a);
    }

}
