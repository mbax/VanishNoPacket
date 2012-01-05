package org.kitteh.vanish.sniffers;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet41MobEffect;

import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishManager;

public class Sniffer41MobEffect extends Sniffer {

    public Sniffer41MobEffect(VanishManager vanish) {
        super(vanish);
    }

    @Override
    public boolean checkPakkit(Player player, Packet packet) throws ClassCastException {
        return !this.vanish.shouldHide(player, ((Packet41MobEffect) packet).a);
    }

}
