package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer42RemoveMobEffect extends Sniffer {

    public Sniffer42RemoveMobEffect(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet42RemoveMobEffect) packet).a);
    }

}
