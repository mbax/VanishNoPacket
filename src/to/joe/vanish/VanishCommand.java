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
            if (label.equals("np")) {
                this.toggle(player, "nopickup");
                return true;
            }
            if (label.equals("nf")) {
                this.toggle(player, "nofollow");
                return true;
            }
            if (label.equals("ni")) {
                this.toggle(player, "nointeract");
                return true;
            }
            if ((args.length == 0)) {
                if (VanishPerms.canVanish((Player) sender)) {
                    this.plugin.getManager().toggleVanish(player);
                }
            } else if (args[0].equalsIgnoreCase("check") && VanishPerms.canVanish((Player) sender)) {
                if (this.plugin.getManager().isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are invisible.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are not invisible.");
                }
            } else if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {
                if (args.length == 1) {
                    final StringBuilder toggleList = new StringBuilder();
                    if (VanishPerms.canToggleSee(player)) {
                        toggleList.append(this.colorize(VanishPerms.canSeeAll(player)) + "see" + ChatColor.DARK_AQUA);
                        this.plugin.getManager().resetSeeing(player);
                    }
                    if (VanishPerms.canToggleNoPickup(player)) {
                        this.comma(toggleList, this.colorize(VanishPerms.canNotPickUp(player)) + "nopickup" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleNoFollow(player)) {
                        this.comma(toggleList, this.colorize(VanishPerms.canNotFollow(player)) + "nofollow" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleNoInteract(player)) {
                        this.comma(toggleList, this.colorize(VanishPerms.canNotInteract(player)) + "nointeract" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleDamageIn(player)) {
                        this.comma(toggleList, this.colorize(VanishPerms.blockIncomingDamage(player)) + "damage-in" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleDamageOut(player)) {
                        this.comma(toggleList, this.colorize(VanishPerms.blockOutgoingDamage(player)) + "damage-out" + ChatColor.DARK_AQUA);
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
                    this.toggle(player, args[1]);
                }
            } else if ((args[0].equalsIgnoreCase("fakequit") || args[0].equalsIgnoreCase("fq")) && VanishPerms.canFakeAnnounce(player)) {
                if (!this.plugin.getManager().isVanished(player)) {
                    this.plugin.getManager().toggleVanish(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Already invisible :)");
                }
                this.plugin.getManager().getAnnounceManipulator().fakeQuit(player);
            } else if ((args[0].equalsIgnoreCase("fakejoin") || args[0].equalsIgnoreCase("fj")) && VanishPerms.canFakeAnnounce(player)) {
                if (this.plugin.getManager().isVanished(player)) {
                    this.plugin.getManager().toggleVanish(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Already visible :)");
                }
                this.plugin.getManager().getAnnounceManipulator().fakeJoin(player);
            }
        }
        return true;
    }

    private String colorize(boolean has) {
        if (has) {
            return ChatColor.GREEN.toString();
        } else {
            return ChatColor.RED.toString();
        }
    }

    private void comma(StringBuilder builder, String string) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(string);
    }

    private void toggle(Player player, String toggle) {
        final StringBuilder message = new StringBuilder();
        boolean status = false;;
        if (toggle.equalsIgnoreCase("see") && VanishPerms.canToggleSee(player)) {
            status = VanishPerms.toggleSeeAll(player);
            message.append("see all");
        } else if (toggle.equalsIgnoreCase("nopickup") && VanishPerms.canToggleNoPickup(player)) {
            status = VanishPerms.toggleNoPickup(player);
            message.append("no pickup");
        } else if (toggle.equalsIgnoreCase("nofollow") && VanishPerms.canToggleNoFollow(player)) {
            status = VanishPerms.toggleNoFollow(player);
            message.append("no mob follow");
        } else if (toggle.equalsIgnoreCase("damage-in") && VanishPerms.canToggleDamageIn(player)) {
            status = VanishPerms.toggleDamageIn(player);
            message.append("block incoming damage");
        } else if (toggle.equalsIgnoreCase("damage-out") && VanishPerms.canToggleDamageOut(player)) {
            status = VanishPerms.toggleDamageOut(player);
            message.append("block outgoing damage");
        } else if (toggle.equalsIgnoreCase("nointeract") && VanishPerms.canToggleNoInteract(player)) {
            status = VanishPerms.toggleNoInteract(player);
            message.append("no interact");
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
}
