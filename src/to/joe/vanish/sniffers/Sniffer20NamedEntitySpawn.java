package to.joe.vanish.sniffers;

import net.minecraft.server.Packet20NamedEntitySpawn;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;

import to.joe.vanish.VanishManager;

public class Sniffer20NamedEntitySpawn extends Sniffer {

    public Sniffer20NamedEntitySpawn(VanishManager vanish) {
        super(net.minecraft.server.Packet20NamedEntitySpawn.class, vanish);
    }

    @Override
    public boolean checkPacket2(Player player, MCPacket packet) {
        final Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn) ((MCCraftPacket) packet).getPacket();
        final String name = packet20.b;
        if (this.vanish.getPlugin().colorationEnabled() && this.vanish.isVanished(name)) {
            packet20.b = ChatColor.DARK_AQUA + name;
            if (packet20.b.length() > 15) {
                packet20.b = packet20.b.substring(0, 15);
            }
        }
        return !this.vanish.shouldHide(player, packet20.a);
    }

}
