package to.joe.vanish.sniffers;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

@SuppressWarnings("rawtypes")
public abstract class Sniffer implements PacketListener {

    private final Class type;
    protected VanishManager vanish;

    public Sniffer(Class type, VanishManager vanish) {
        this.type = type;
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        if (!this.vanish.sanityCheck(((MCCraftPacket) packet).getPacket().getClass(), this.type)) {
            return true;
        }
        return this.checkPacket2(player, packet);
    }

    public abstract boolean checkPacket2(Player player, MCPacket packet);

}
