package to.joe.vanish.sniffers;

import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer29DestroyEntity extends Sniffer {

    public Sniffer29DestroyEntity(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) throws ClassCastException {
        final Packet29DestroyEntity packit = ((Packet29DestroyEntity) ((MCCraftPacket) packet).getPacket());
        if (packit instanceof VanishManager.Hat) {
            ((MCCraftPacket) packet).setPacket(new Packet29DestroyEntity(packit.a), 29);
            return true;
        }
        return !this.vanish.shouldHide(player, packit.a);
    }

}
