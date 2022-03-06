/*
 * VanishNoPacket
 * Copyright (C) 2011-2022 Matt Baxter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.kitteh.vanish;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class VanishCommand implements CommandExecutor {
    private final VanishPlugin plugin;

    public VanishCommand(@NonNull VanishPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        // Plain /vanish
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (VanishPerms.canVanish(player)) {
                    this.plugin.getManager().toggleVanish(player);
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
                sender.sendMessage(Component.text().content("[Vanish] Users reloaded").color(Settings.getDark()));
                sender.sendMessage(Component.text().content("[Vanish] Some settings refreshed").color(Settings.getDark()));
            } else {
                this.denied(sender);
            }
            return true;
        }
        if (goal.equalsIgnoreCase("list")) {
            if (VanishPerms.canList(sender)) {
                final TextComponent.Builder builder = Component.text();
                builder.color(Settings.getDark());
                builder.content("Vanished: ");
                int count = 0;
                for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
                    if (this.plugin.getManager().isVanished(player)) {
                        if (count++ > 0) {
                            builder.append(Component.text(","));
                        }
                        builder.append(Component.text().content(player.getName()).color(Settings.getLight()));
                    }
                }
                sender.sendMessage(builder);
            } else {
                this.denied(sender);
            }
            return true;
        }
        // Goodbye console!
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component.text()
                    .content("Did you mean ")
                    .color(Settings.getLight())
                    .append(Component.text().content("vanish reload").color(NamedTextColor.WHITE))
                    .append(Component.text(" or "))
                    .append(Component.text().content("vanish list").color(NamedTextColor.WHITE))
                    .append(Component.text("?")));
            return true;
        }
        // No more console options below this point

        // Check if I'm vanished
        if (goal.equalsIgnoreCase("check")) {
            if (this.plugin.getManager().isVanished(player)) {
                player.sendMessage(Component.text().color(Settings.getDark()).content("You are invisible."));
            } else {
                player.sendMessage(Component.text().color(Settings.getDark()).content("You are visible."));
            }
            return true;
        }
        // Toggling
        if (goal.equalsIgnoreCase("toggle") || goal.equalsIgnoreCase("t")) {
            // List my toggles
            if (args.length == 1) {
                final List<ComponentLike> toggles = new ArrayList<>();
                if (VanishPerms.canToggleSee(player)) {
                    toggles.add(this.colorize(VanishPerms.canSeeAll(player), "see"));
                }
                if (VanishPerms.canToggleNoPickup(player)) {
                    toggles.add(this.colorize(VanishPerms.canNotPickUp(player), "nopickup"));
                }
                if (VanishPerms.canToggleNoFollow(player)) {
                    toggles.add(this.colorize(VanishPerms.canNotFollow(player), "nofollow"));
                }
                if (VanishPerms.canToggleNoInteract(player)) {
                    toggles.add(this.colorize(VanishPerms.canNotInteract(player), "nointeract"));
                }
                if (VanishPerms.canToggleDamageIn(player)) {
                    toggles.add(this.colorize(VanishPerms.blockIncomingDamage(player), "damage-in"));
                }
                if (VanishPerms.canToggleDamageOut(player)) {
                    toggles.add(this.colorize(VanishPerms.blockOutgoingDamage(player), "damage-out"));
                }
                if (VanishPerms.canToggleNoChat(player)) {
                    toggles.add(this.colorize(VanishPerms.canNotChat(player), "nochat"));
                }
                if (VanishPerms.canToggleNoHunger(player)) {
                    toggles.add(this.colorize(VanishPerms.canNotHunger(player), "nohunger"));
                }
                if (VanishPerms.canToggleSilentChestReads(player)) {
                    toggles.add(this.colorize(VanishPerms.canReadChestsSilently(player), "chests"));
                }
                if (toggles.isEmpty()) {
                    player.sendMessage(Component.text().color(Settings.getDark()).content("You cannot toggle anything"));
                } else {
                    TextComponent.Builder builder = Component.text().color(Settings.getDark()).content("You can toggle: ");
                    this.buildList(builder, toggles);
                    player.sendMessage(builder);
                }
            } else {
                // I wish to toggle something
                this.toggle(player, args[1]);
            }
            return true;
        }

        if (goal.equalsIgnoreCase("effects") || goal.equalsIgnoreCase("e")) {
            // List my toggles
            if (args.length == 1) {
                final List<ComponentLike> toggles = new ArrayList<>();
                if (VanishPerms.canToggleSmoke(player)) {
                    toggles.add(this.colorize(VanishPerms.canEffectSmoke(player), "smoke"));
                }
                if (VanishPerms.canToggleEffectExplode(player)) {
                    toggles.add(this.colorize(VanishPerms.canEffectExplode(player), "explode"));
                }
                if (VanishPerms.canToggleEffectLightning(player)) {
                    toggles.add(this.colorize(VanishPerms.canEffectLightning(player), "lightning"));
                }
                if (VanishPerms.canToggleEffectFlames(player)) {
                    toggles.add(this.colorize(VanishPerms.canEffectFlames(player), "flames"));
                }
                if (VanishPerms.canToggleEffectBats(player)) {
                    toggles.add(this.colorize(VanishPerms.canEffectBats(player), "bats"));
                }
                if (toggles.isEmpty()) {
                    player.sendMessage(Component.text().color(Settings.getDark()).content("You cannot toggle any effects"));
                } else {
                    TextComponent.Builder builder = Component.text().color(Settings.getDark()).content("You can toggle: ");
                    this.buildList(builder, toggles);
                    player.sendMessage(builder);
                }
            } else {
                // I wish to toggle something
                this.toggle(player, args[1]);
            }
            return true;
        }

        // The non-toggles
        if (goal.equalsIgnoreCase("on")) {
            if (!VanishPerms.canVanish(player)) {
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
            if (!VanishPerms.canVanish(player)) {
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
                    player.sendMessage(Component.text().color(NamedTextColor.RED).content("Already invisible :)"));
                }
                boolean forced = (args.length > 1) && (args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("force"));
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
                    player.sendMessage(Component.text().color(NamedTextColor.RED).content("Already visible :)"));
                }
                boolean forced = (args.length > 1) && (args[1].equalsIgnoreCase("f") || args[1].equalsIgnoreCase("force"));
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

    private void buildList(TextComponent.@NonNull Builder builder, @NonNull List<ComponentLike> components) {
        for (int i = 0; i < components.size(); i++) {
            if (i > 0) {
                builder.append(Component.text(", "));
            }
            builder.append(components.get(i));
        }
    }

    private @NonNull ComponentLike colorize(boolean has, String name) {
        return Component.text().color(has ? NamedTextColor.GREEN : NamedTextColor.RED).content(name);
    }

    private void denied(@NonNull CommandSender sender) {
        sender.sendMessage(Component.text().color(Settings.getLight()).content("[Vanish] ").append(Component.text().color(Settings.getDark()).content("Access denied.")));
    }

    private void toggle(@NonNull Player player, @NonNull String toggle) {
        String name = null;
        boolean status = false;
        if (toggle.equalsIgnoreCase("see") && VanishPerms.canToggleSee(player)) {
            status = VanishPerms.toggleSeeAll(player);
            this.plugin.getManager().resetSeeing(player);
            name = "see all";
        } else if (toggle.equalsIgnoreCase("nopickup") && VanishPerms.canToggleNoPickup(player)) {
            status = VanishPerms.toggleNoPickup(player);
            name = "no pickup";
        } else if (toggle.equalsIgnoreCase("nofollow") && VanishPerms.canToggleNoFollow(player)) {
            status = VanishPerms.toggleNoFollow(player);
            name = "no mob follow";
        } else if (toggle.equalsIgnoreCase("damage-in") && VanishPerms.canToggleDamageIn(player)) {
            status = VanishPerms.toggleDamageIn(player);
            name = "block incoming damage";
        } else if (toggle.equalsIgnoreCase("damage-out") && VanishPerms.canToggleDamageOut(player)) {
            status = VanishPerms.toggleDamageOut(player);
            name = "block outgoing damage";
        } else if (toggle.equalsIgnoreCase("nointeract") && VanishPerms.canToggleNoInteract(player)) {
            status = VanishPerms.toggleNoInteract(player);
            name = "no interact";
        } else if (toggle.equalsIgnoreCase("nochat") && VanishPerms.canToggleNoChat(player)) {
            status = VanishPerms.toggleNoChat(player);
            name = "no chat";
        } else if (toggle.equalsIgnoreCase("nohunger") && VanishPerms.canToggleNoHunger(player)) {
            status = VanishPerms.toggleNoHunger(player);
            name = "no hunger";
        } else if (toggle.equalsIgnoreCase("chests") && VanishPerms.canToggleSilentChestReads(player)) {
            status = VanishPerms.toggleSilentChestReads(player);
            name = "silent chest reads";
        } else if (toggle.equalsIgnoreCase("smoke") && VanishPerms.canToggleSmoke(player)) {
            status = VanishPerms.toggleEffectSmoke(player);
            name = "smoke effect";
        } else if (toggle.equalsIgnoreCase("explode") && VanishPerms.canToggleEffectExplode(player)) {
            status = VanishPerms.toggleEffectExplode(player);
            name = "explosion effect";
        } else if (toggle.equalsIgnoreCase("lightning") && VanishPerms.canToggleEffectLightning(player)) {
            status = VanishPerms.toggleEffectLightning(player);
            name = "lightning effect";
        } else if (toggle.equalsIgnoreCase("flames") && VanishPerms.canToggleEffectFlames(player)) {
            status = VanishPerms.toggleEffectFlames(player);
            name = "flames effect";
        } else if (toggle.equalsIgnoreCase("bats") && VanishPerms.canToggleEffectBats(player)) {
            status = VanishPerms.toggleEffectBats(player);
            name = "bats effect";
        }
        if (name != null) {
            player.sendMessage(Component.text().color(Settings.getDark()).content("Status: " + name + ": " + (status ? "enabled" : "disabled")));
        } else if (VanishPerms.canVanish(player)) {
            player.sendMessage(Component.text().color(Settings.getDark()).content("You can't toggle that!"));
        }
    }
}
