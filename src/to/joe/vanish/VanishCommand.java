package to.joe.vanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final VanishPlugin plugin;

    public VanishCommand(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player) && (Perms.canVanish((Player) sender))) {
            this.plugin.getManager().toggleVanish((Player) sender);
        }
        return true;
    }
}
