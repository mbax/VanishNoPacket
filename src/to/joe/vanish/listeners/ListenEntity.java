package to.joe.vanish.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import to.joe.vanish.VanishPerms;
import to.joe.vanish.VanishPlugin;

public class ListenEntity extends EntityListener {

    private final VanishPlugin plugin;

    public ListenEntity(VanishPlugin instance) {
        this.plugin = instance;
    }

    public void message(String message) {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        final Entity smacked = event.getEntity();
        if (smacked instanceof Player) {
            final Player player = (Player) smacked;
            if (this.plugin.getManager().isVanished(player) && VanishPerms.blockIncomingDamage(player)) {
                event.setCancelled(true);
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            final Entity damager = ev.getDamager();
            if (damager instanceof Player) {
                final Player player = (Player) damager;
                if (this.plugin.getManager().isVanished(player) && VanishPerms.blockOutgoingDamage(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getTarget()) && VanishPerms.canNotFollow((Player) event.getTarget())) {
            event.setCancelled(true);
        }
    }

}
