package to.joe.vanish.sniffers;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer29DestroyEntity implements PacketListener {

    private final VanishManager vanish;

    public Sniffer29DestroyEntity(VanishManager vanish) {
        this.vanish = vanish;
    }

    @Override
    public boolean checkPacket(Player player, MCPacket packet) {
        final Packet29DestroyEntity packit = ((Packet29DestroyEntity) ((MCCraftPacket) packet).getPacket());
        if (packit instanceof VanishManager.Hat) {
            ((MCCraftPacket) packet).setPacket(new Packet29DestroyEntity(packit.a), 29);
            return true;
        }
        return !this.vanish.shouldHide(player, packit.a);
    }

}
