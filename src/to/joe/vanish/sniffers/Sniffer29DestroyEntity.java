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
        return !this.vanish.shouldHide(player, ((Packet29DestroyEntity) ((MCCraftPacket) packet).getPacket()).a);
    }

}
