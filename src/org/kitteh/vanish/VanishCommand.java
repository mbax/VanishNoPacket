package org.kitteh.vanish;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.vanish.metrics.MetricsOverlord;

public class VanishCommand implements CommandExecutor {

    private final VanishPlugin plugin;

    public VanishCommand(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MetricsOverlord.command.increment();
        if ((args.length > 0) && args[0].equalsIgnoreCase("reload") && VanishPerms.canReload(sender)) {
            this.plugin.reload();
            sender.sendMessage(ChatColor.DARK_AQUA + "[Vanish] Users reloaded");
            sender.sendMessage(ChatColor.DARK_AQUA + "[Vanish] Some settings refreshed");
            return true;
        } else if ((args.length > 0) && args[0].equalsIgnoreCase("list") && VanishPerms.canList(sender)) {
            final StringBuilder list = new StringBuilder();
            for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                if ((player != null) && this.plugin.getManager().isVanished(player)) {
                    if (list.length() > 0) {
                        list.append(ChatColor.DARK_AQUA);
                        list.append(',');
                    }
                    list.append(ChatColor.AQUA);
                    list.append(player.getName());
                }
            }
            list.insert(0, "Vanished: ");
            list.insert(0, ChatColor.DARK_AQUA);
            sender.sendMessage(list.toString());
            return true;
        }
        if ((sender instanceof Player)) {
            final Player player = (Player) sender;
            if (label.equals("np")) {
                return this.toggle(player, "nopickup");
            }
            if (label.equals("nf")) {
                return this.toggle(player, "nofollow");
            }
            if (label.equals("ni")) {
                return this.toggle(player, "nointeract");
            }
            if (label.equals("nc")) {
                return this.toggle(player, "nochat");
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
                    final StringBuilder toggleReply = new StringBuilder();
                    if (VanishPerms.canToggleSee(player)) {
                        toggleReply.append(this.colorize(VanishPerms.canSeeAll(player)) + "see" + ChatColor.DARK_AQUA);
                        this.plugin.getManager().resetSeeing(player);
                    }
                    if (VanishPerms.canToggleNoPickup(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.canNotPickUp(player)) + "nopickup" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleNoFollow(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.canNotFollow(player)) + "nofollow" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleNoInteract(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.canNotInteract(player)) + "nointeract" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleDamageIn(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.blockIncomingDamage(player)) + "damage-in" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleDamageOut(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.blockOutgoingDamage(player)) + "damage-out" + ChatColor.DARK_AQUA);
                    }
                    if (VanishPerms.canToggleNoChat(player)) {
                        this.appendList(toggleReply, this.colorize(VanishPerms.canNotChat(player)) + "nochat" + ChatColor.DARK_AQUA);
                    }
                    if (toggleReply.length() > 0) {
                        toggleReply.insert(0, ChatColor.DARK_AQUA + "You can toggle: ");
                    } else {
                        if (VanishPerms.canVanish((Player) sender)) {
                            toggleReply.append(ChatColor.DARK_AQUA + "You cannot toggle anything");
                        }
                    }
                    if (toggleReply.length() > 0) {
                        player.sendMessage(toggleReply.toString());
                    }
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

    private void appendList(StringBuilder builder, String string) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(string);
    }

    private String colorize(boolean has) {
        if (has) {
            return ChatColor.GREEN.toString();
        } else {
            return ChatColor.RED.toString();
        }
    }

    private boolean toggle(Player player, String toggle) {
        final StringBuilder message = new StringBuilder();
        MetricsOverlord.toggle.increment();
        boolean status = false;
        if (toggle.equalsIgnoreCase("see") && VanishPerms.canToggleSee(player)) {
            status = VanishPerms.toggleSeeAll(player);
            this.plugin.getManager().resetSeeing(player);
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
        } else if (toggle.equalsIgnoreCase("nochat") && VanishPerms.canToggleNoChat(player)) {
            status = VanishPerms.toggleNoChat(player);
            message.append("no chat");
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
        return true;
    }
}
