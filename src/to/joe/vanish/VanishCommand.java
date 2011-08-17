package to.joe.vanish;

import org.bukkit.ChatColor;
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
        if ((sender instanceof Player) && Perms.canVanish((Player) sender)) {
            final Player player = (Player) sender;
            if (args.length == 0) {
                this.plugin.getManager().toggleVanish(player);
            } else if (args[0].equals("check")) {
                if (this.plugin.getManager().isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are invisible.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are not invisible.");
                }
            }
        }
        return true;
    }
}
