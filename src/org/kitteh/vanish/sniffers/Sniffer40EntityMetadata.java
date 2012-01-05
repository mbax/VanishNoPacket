package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer40EntityMetadata extends Sniffer {

    public Sniffer40EntityMetadata(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet40EntityMetadata) packet).a);
    }

}
