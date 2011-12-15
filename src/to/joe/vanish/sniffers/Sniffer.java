package to.joe.vanish.sniffers;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public abstract class Sniffer implements PacketListener {

    protected VanishManager vanish;

    public Sniffer(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        try {
            return this.checkPacket2(player, packet);
        } catch (ClassCastException e) {
            this.vanish.sanityCheck(e);
        }
        return true;
    }

    public abstract boolean checkPacket2(Player player, MCPacket packet) throws ClassCastException;

}
