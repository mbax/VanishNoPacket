package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet20NamedEntitySpawn;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishManager;

public class Sniffer20NamedEntitySpawn extends Sniffer {

    public Sniffer20NamedEntitySpawn(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        final Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn) packet;
        final String name = packet20.b;
        if (Settings.enableColoration() && this.vanish.isVanished(name)) {
            packet20.b = ChatColor.DARK_AQUA + name;
            if (packet20.b.length() > 15) {
                packet20.b = packet20.b.substring(0, 15);
            }
        }
        return !this.vanish.shouldHide(player, packet20.a);
    }

}
