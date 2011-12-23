package to.joe.vanish.sniffers;

import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer201PlayerInfo extends Sniffer {

    public Sniffer201PlayerInfo(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, MCPacket packet) throws ClassCastException {
        final Packet201PlayerInfo packit = ((Packet201PlayerInfo) ((MCCraftPacket) packet).getPacket());
        return !this.vanish.shouldHide(player, packit.a, packit.b);
    }

}
