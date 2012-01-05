package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet33RelEntityMoveLook;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer33RelEntityMoveLook extends Sniffer {

    public Sniffer33RelEntityMoveLook(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet33RelEntityMoveLook) packet).a);
    }

}
