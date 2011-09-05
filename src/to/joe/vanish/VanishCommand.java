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
        if ((sender instanceof Player)) {
            final Player player = (Player) sender;
            if ((args.length == 0) && VanishPerms.canVanish((Player) sender)) {
                this.plugin.getManager().toggleVanish(player);
            } else if (args[0].equalsIgnoreCase("check") && VanishPerms.canVanish((Player) sender)) {
                if (this.plugin.getManager().isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are invisible.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are not invisible.");
                }
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (args.length == 1) {
                    final StringBuilder toggleList = new StringBuilder();
                    if (VanishPerms.canToggleSee(player)) {
                        toggleList.append("see");
                        this.plugin.getManager().resetSeeing(player);
                    }
                    if (VanishPerms.canToggleNoPickup(player)) {
                        this.comma(toggleList, "nopickup");
                    }
                    if (VanishPerms.canToggleNoFollow(player)) {
                        this.comma(toggleList, "nofollow");
                    }
                    if (VanishPerms.canToggleDamageIn(player)) {
                        this.comma(toggleList, "damage-in");
                    }
                    if (VanishPerms.canToggleDamageOut(player)) {
                        this.comma(toggleList, "damage-out");
                    }
                    if (toggleList.length() > 0) {
                        toggleList.insert(0, ChatColor.DARK_AQUA + "You can toggle: ");
                    } else {
                        if (VanishPerms.canVanish((Player) sender)) {
                            toggleList.append(ChatColor.DARK_AQUA + "You cannot toggle anything");
                        }
                    }
                    player.sendMessage(toggleList.toString());
                } else {
                    final StringBuilder message = new StringBuilder();
                    boolean status = false;;
                    if (args[1].equalsIgnoreCase("see") && VanishPerms.canToggleSee(player)) {
                        status = VanishPerms.toggleSeeAll(player);
                        message.append("see all");
                    } else if (args[1].equalsIgnoreCase("nopickup") && VanishPerms.canToggleNoPickup(player)) {
                        status = VanishPerms.toggleNoPickup(player);
                        message.append("no pickup");
                    } else if (args[1].equalsIgnoreCase("nofollow") && VanishPerms.canToggleNoFollow(player)) {
                        status = VanishPerms.toggleNoFollow(player);
                        message.append("no mob follow");
                    } else if (args[1].equalsIgnoreCase("damage-in") && VanishPerms.canToggleDamageIn(player)) {
                        status = VanishPerms.toggleDamageIn(player);
                        message.append("block incoming damage");
                    } else if (args[1].equalsIgnoreCase("damage-out") && VanishPerms.canToggleDamageOut(player)) {
                        status = VanishPerms.toggleDamageOut(player);
                        message.append("block outgoing damage");
                    }
                    if (message.length() > 0) {
                        message.insert(0, ChatColor.DARK_AQUA + "Status: ");
                        message.append(": ");
                        if (status) {
                            message.append("enabled");
                        } else {
                            message.append("disabled");
                        }
                        player.sendMessage(message.toString());
                    } else if (VanishPerms.canVanish(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "You can't toggle that!");
                    }
                }
            } else if (args[1].equalsIgnoreCase("fakequit") && VanishPerms.canFakeAnnounce(player)) {
                this.plugin.getManager().getAnnounceManipulator().fakeQuit(player.getName());
            } else if (args[1].equalsIgnoreCase("fakejoin") && VanishPerms.canFakeAnnounce(player)) {
                this.plugin.getManager().getAnnounceManipulator().fakeJoin(player.getName());
            }
        }
        return true;
    }

    private void comma(StringBuilder builder, String string) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(string);
    }
}
