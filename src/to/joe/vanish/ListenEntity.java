package to.joe.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class ListenEntity extends EntityListener {

    private final VanishPlugin plugin;

    public ListenEntity(VanishPlugin instance) {
        this.plugin = instance;
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getTarget()) && Perms.canNotFollow((Player) event.getTarget())) {
            event.setCancelled(true);
        }
    }

}
