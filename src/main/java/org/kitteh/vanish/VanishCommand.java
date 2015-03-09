package org.kitteh.vanish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.vanish.metrics.MetricsOverlord;

public final class VanishCommand implements CommandExecutor {
    private final VanishPlugin plugin;

    public VanishCommand(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MetricsOverlord.getCommandTracker().increment();
        // First, the short aliases
        if (label.length() == 2) {
            if (sender instanceof Player) {
                if (label.equals("np")) {
                    this.toggle((Player) sender, "nopickup");
                }
                if (label.equals("nf")) {
                    this.toggle((Player) sender, "nofollow");
                }
                if (label.equals("nh")) {
                    this.toggle((Player) sender, "nohunger");
                }
                if (label.equals("ni")) {
                    this.toggle((Player) sender, "nointeract");
                }
                if (label.equals("nc")) {
                    this.toggle((Player) sender, "nochat");
                }
            }
            return true;
        }
        // Plain /vanish
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (VanishPerms.canVanish((Player) sender)) {
                    this.plugin.getManager().toggleVanish((Player) sender);
                } else {
                    this.denied(sender);
                }
            }
            return true;
        }
        // /vanish <goal> [maybe stuff here]
        final String goal = args[0];
        if (goal.equalsIgnoreCase("reload")) {
            if (VanishPerms.canReload(sender)) {
                this.plugin.reload();
                sender.sendMessage(ChatColor.DARK_AQUA + "[Vanish] Users reloaded");
                sender.sendMessage(ChatColor.DARK_AQUA + "[Vanish] Some settings refreshed");
            } else {
                this.denied(sender);
            }
            return true;
        }
        if (goal.equalsIgnoreCase("list")) {            
            if (VanishPerms.canList(sender)) {
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
            } else {
                this.denied(sender);
            }
            return true;
        }
        // Goodbye console!
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.AQUA + "Did you mean " + ChatColor.WHITE + "vanish reload" + ChatColor.AQUA + " or " + ChatColor.WHITE + "vanish list" + ChatColor.AQUA + "?");
            return true;
        }
        // No more console options below this point
        final Player player = (Player) sender;

        // Check if I'm vanished
        if (goal.equalsIgnoreCase("check")) {
            if (this.plugin.getManager().isVanished(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + "You are invisible.");
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "You are visible.");
            }
            return true;
        }
        // Toggling
        if (goal.equalsIgnoreCase("toggle") || goal.equalsIgnoreCase("t")) {
            // List my toggles
            if (args.length == 1) {
                final StringBuilder toggleReply = new StringBuilder();
                if (VanishPerms.canToggleSee(player)) {
                    toggleReply.append(this.colorize(VanishPerms.canSeeAll(player)) + "see" + ChatColor.DARK_AQUA);
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
                if (VanishPerms.canToggleNoHunger(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canNotHunger(player)) + "nohunger" + ChatColor.DARK_AQUA);
                }
                if (VanishPerms.canToggleSilentChestReads(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canReadChestsSilently(player)) + "chests" + ChatColor.DARK_AQUA);
                }
                if (toggleReply.length() > 0) {
                    toggleReply.insert(0, ChatColor.DARK_AQUA + "You can toggle: ");
                } else {
                    toggleReply.append(ChatColor.DARK_AQUA + "You cannot toggle anything");
                }
                player.sendMessage(toggleReply.toString());
            } else {
                // I wish to toggle something
                this.toggle(player, args[1]);
            }
            return true;
        }

        if (goal.equalsIgnoreCase("effects") || goal.equalsIgnoreCase("e")) {
            // List my toggles
            if (args.length == 1) {
                final StringBuilder toggleReply = new StringBuilder();
                if (VanishPerms.canToggleSmoke(player)) {
                    toggleReply.append(this.colorize(VanishPerms.canEffectSmoke(player)) + "smoke" + ChatColor.DARK_AQUA);
                }
                if (VanishPerms.canToggleEffectExplode(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canEffectExplode(player)) + "explode" + ChatColor.DARK_AQUA);
                }
                if (VanishPerms.canToggleEffectLightning(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canEffectLightning(player)) + "lightning" + ChatColor.DARK_AQUA);
                }
                if (VanishPerms.canToggleEffectFlames(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canEffectFlames(player)) + "flames" + ChatColor.DARK_AQUA);
                }
                if (VanishPerms.canToggleEffectBats(player)) {
                    this.appendList(toggleReply, this.colorize(VanishPerms.canEffectBats(player)) + "bats" + ChatColor.DARK_AQUA);
                }
                if (toggleReply.length() > 0) {
                    toggleReply.insert(0, ChatColor.DARK_AQUA + "You can toggle: ");
                } else {
                    toggleReply.append(ChatColor.DARK_AQUA + "You cannot toggle any effects");
                }
                player.sendMessage(toggleReply.toString());
            } else {
                // I wish to toggle something
                this.toggle(player, args[1]);
            }
            return true;
        }

        // The non-toggles
        if (goal.equalsIgnoreCase("on")) {
            if (!VanishPerms.canVanishOn(player)) {
                this.denied(sender);
                return true;
            }
            if (!this.plugin.getManager().isVanished(player)) {
                this.plugin.getManager().toggleVanish(player);
                // Fake announce as well?
                if ((args.length > 1) && args[1].equalsIgnoreCase("fake") && VanishPerms.canFakeAnnounce(player)) {
                    this.plugin.getManager().getAnnounceManipulator().fakeQuit(player, false);
                }
            }
            return true;
        }
        if (goal.equalsIgnoreCase("off")) {
            if (!VanishPerms.canVanishOff(player)) {
                this.denied(sender);
                return true;
            }
            if (this.plugin.getManager().isVanished(player)) {
                this.plugin.getManager().toggleVanish(player);
                // Fake announce as well?
                if ((args.length > 1) && args[1].equalsIgnoreCase("fake") && VanishPerms.canFakeAnnounce(player)) {
                    this.plugin.getManager().getAnnounceManipulator().fakeJoin(player, false);
                }
            }
            return true;
        }

        // Below this point, user must be able to /vanish

        if (!VanishPerms.canVanish(player)) {
            this.denied(sender);
            return true;
        }

        // Fake announces. Requires vanish.fakeannounce
        if ((goal.equalsIgnoreCase("fakequit") || goal.equalsIgnoreCase("fq"))) {
            if (VanishPerms.canFakeAnnounce(player)) {
                if (!this.plugin.getManager().isVanished(player)) {
                    this.plugin.getManager().toggleVanish(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Already invisible :)");
                }
                boolean forced = false;
                if ((args.length > 1) && (args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("force"))) {
                    forced = true;
                }
                this.plugin.getManager().getAnnounceManipulator().fakeQuit(player, forced);
            } else {
                this.denied(sender);
            }
            return true;
        }
        if ((goal.equalsIgnoreCase("fakejoin") || goal.equalsIgnoreCase("fj"))) {
            if (VanishPerms.canFakeAnnounce(player)) {
                if (this.plugin.getManager().isVanished(player)) {
                    this.plugin.getManager().toggleVanish(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Already visible :)");
                }
                boolean forced = false;
                if ((args.length > 1) && (args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("force"))) {
                    forced = true;
                }
                this.plugin.getManager().getAnnounceManipulator().fakeJoin(player, forced);
            } else {
                this.denied(sender);
            }
            return true;
        }

        // Continue? 

        // 3

        // 2

        // 1

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

    private void denied(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[Vanish] " + ChatColor.DARK_AQUA + "Access denied.");
    }

    private void toggle(Player player, String toggle) {
        final StringBuilder message = new StringBuilder();
        MetricsOverlord.getToggleTracker().increment();
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
        } else if (toggle.equalsIgnoreCase("nohunger") && VanishPerms.canToggleNoHunger(player)) {
            status = VanishPerms.toggleNoHunger(player);
            message.append("no hunger");
        } else if (toggle.equalsIgnoreCase("chests") && VanishPerms.canToggleSilentChestReads(player)) {
            status = VanishPerms.toggleSilentChestReads(player);
            message.append("silent chest reads");
        } else if (toggle.equalsIgnoreCase("smoke") && VanishPerms.canToggleSmoke(player)) {
            status = VanishPerms.toggleEffectSmoke(player);
            message.append("smoke effect");
        } else if (toggle.equalsIgnoreCase("explode") && VanishPerms.canToggleEffectExplode(player)) {
            status = VanishPerms.toggleEffectExplode(player);
            message.append("explosion effect");
        } else if (toggle.equalsIgnoreCase("lightning") && VanishPerms.canToggleEffectLightning(player)) {
            status = VanishPerms.toggleEffectLightning(player);
            message.append("lightning effect");
        } else if (toggle.equalsIgnoreCase("flames") && VanishPerms.canToggleEffectFlames(player)) {
            status = VanishPerms.toggleEffectFlames(player);
            message.append("flames effect");
        } else if (toggle.equalsIgnoreCase("bats") && VanishPerms.canToggleEffectBats(player)) {
            status = VanishPerms.toggleEffectBats(player);
            message.append("bats effect");
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