package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer201PlayerInfo extends Sniffer {

    public Sniffer201PlayerInfo(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        final Packet201PlayerInfo packit = ((Packet201PlayerInfo) packet);
        return !this.vanish.shouldHide(player, packit.a, packit.b);
    }

}
