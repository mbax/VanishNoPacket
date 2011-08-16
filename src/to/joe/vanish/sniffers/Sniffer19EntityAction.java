package to.joe.vanish.sniffers;

import net.minecraft.server.Packet19EntityAction;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer19EntityAction implements PacketListener {

    private final VanishManager vanish;

    public Sniffer19EntityAction(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        return !this.vanish.shouldHide(player, ((Packet19EntityAction) ((MCCraftPacket) packet).getPacket()).a);
    }

}
