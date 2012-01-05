package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet19EntityAction;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer19EntityAction extends Sniffer {

    public Sniffer19EntityAction(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet19EntityAction) packet).a);
    }

}
