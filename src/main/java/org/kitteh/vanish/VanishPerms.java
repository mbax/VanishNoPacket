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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class VanishPerms {
    private static final Map<String, VanishUser> users = Collections.synchronizedMap(new HashMap<>());

    public static boolean blockIncomingDamage(@NonNull Player player) {
        return VanishPerms.getUser(player).getPreventIncomingDamage();
    }

    public static boolean blockOutgoingDamage(@NonNull Player player) {
        return VanishPerms.getUser(player).getPreventOutgoingDamage();
    }

    public static boolean canEffectBats(@NonNull Player player) {
        return VanishPerms.getUser(player).getEffectBats();
    }

    public static boolean canEffectExplode(@NonNull Player player) {
        return VanishPerms.getUser(player).getEffectExplode();
    }

    public static boolean canEffectFlames(@NonNull Player player) {
        return VanishPerms.getUser(player).getEffectFlames();
    }

    public static boolean canEffectLightning(@NonNull Player player) {
        return VanishPerms.getUser(player).getEffectLightning();
    }

    public static boolean canEffectSmoke(@NonNull Player player) {
        return VanishPerms.getUser(player).getEffectSmoke();
    }

    public static boolean canFakeAnnounce(@NonNull Player player) {
        return player.hasPermission("vanish.fakeannounce");
    }

    public static boolean canList(CommandSender sender) {
        return sender.hasPermission("vanish.list");
    }

    public static boolean canNotChat(@NonNull Player player) {
        return VanishPerms.getUser(player).getNoChat();
    }

    public static boolean canNotFollow(@NonNull Player player) {
        return VanishPerms.getUser(player).getNoFollow();
    }

    public static boolean canNotHunger(@NonNull Player player) {
        return VanishPerms.getUser(player).getNoHunger();
    }

    public static boolean canNotInteract(@NonNull Player player) {
        return VanishPerms.getUser(player).getNoInteract();
    }

    public static boolean canNotPickUp(@NonNull Player player) {
        return VanishPerms.getUser(player).getNoPickup();
    }

    public static boolean canNotTrample(@NonNull Player player) {
        return player.hasPermission("vanish.notrample");
    }

    public static boolean canReadChestsSilently(@NonNull Player player) {
        return VanishPerms.getUser(player).getReadChestsSilently();
    }

    public static boolean canReceiveAdminAlerts(@NonNull Player player) {
        return player.hasPermission("vanish.adminalerts");
    }

    public static boolean canReload(CommandSender sender) {
        return sender.hasPermission("vanish.reload");
    }

    public static boolean canSeeAll(@NonNull Player player) {
        return VanishPerms.getUser(player).getSeeAll();
    }

    public static boolean canSeeSpoutStatus(@NonNull Player player) {
        return player.hasPermission("vanish.spout.status");
    }

    public static boolean canSeeStatusUpdates(@NonNull Player player) {
        return player.hasPermission("vanish.statusupdates");
    }

    public static boolean canToggleDamageIn(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.damagein");
    }

    public static boolean canToggleDamageOut(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.damageout");
    }

    public static boolean canToggleEffectBats(@NonNull Player player) {
        return player.hasPermission("vanish.effects.toggle.bats");
    }

    public static boolean canToggleEffectExplode(@NonNull Player player) {
        return player.hasPermission("vanish.effects.toggle.explode");
    }

    public static boolean canToggleEffectFlames(@NonNull Player player) {
        return player.hasPermission("vanish.effects.toggle.flames");
    }

    public static boolean canToggleEffectLightning(@NonNull Player player) {
        return player.hasPermission("vanish.effects.toggle.lightning");
    }

    public static boolean canToggleNoChat(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.nochat");
    }

    public static boolean canToggleNoFollow(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.nofollow");
    }

    public static boolean canToggleNoHunger(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.nohunger");
    }

    public static boolean canToggleNoInteract(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.nointeract");
    }

    public static boolean canToggleNoPickup(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.nopickup");
    }

    public static boolean canToggleSee(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.see");
    }

    public static boolean canToggleSilentChestReads(@NonNull Player player) {
        return player.hasPermission("vanish.toggle.silentchests");
    }

    public static boolean canToggleSmoke(@NonNull Player player) {
        return player.hasPermission("vanish.effects.toggle.smoke");
    }

    public static boolean canVanish(@NonNull Player player) {
        return player.hasPermission("vanish.vanish");
    }

    public static boolean joinVanished(@NonNull Player player) {
        return player.hasPermission("vanish.joinvanished");
    }

    public static boolean joinWithoutAnnounce(@NonNull Player player) {
        return player.hasPermission("vanish.joinwithoutannounce");
    }

    public static boolean permTestOther(@NonNull Player player) {
        return player.hasPermission("vanish.permtest.other");
    }

    public static boolean permTestSelf(@NonNull Player player) {
        return player.hasPermission("vanish.permtest.self");
    }

    public static boolean silentQuit(@NonNull Player player) {
        return player.hasPermission("vanish.silentquit");
    }

    public static boolean toggleDamageIn(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleIncomingDamage();
    }

    public static boolean toggleDamageOut(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleOutgoingDamage();
    }

    public static boolean toggleEffectBats(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleEffectBats();
    }

    public static boolean toggleEffectExplode(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleEffectExplode();
    }

    public static boolean toggleEffectFlames(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleEffectFlames();
    }

    public static boolean toggleEffectLightning(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleEffectLightning();
    }

    public static boolean toggleEffectSmoke(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleEffectSmoke();
    }

    public static boolean toggleNoChat(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleNoChat();
    }

    public static boolean toggleNoFollow(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleNoFollow();
    }

    public static boolean toggleNoHunger(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleNoHunger();
    }

    public static boolean toggleNoInteract(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleNoInteract();
    }

    public static boolean toggleNoPickup(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleNoPickup();
    }

    public static boolean toggleSeeAll(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleSeeAll();
    }

    public static boolean toggleSilentChestReads(@NonNull Player player) {
        return VanishPerms.getUser(player).toggleSilentChestReads();
    }

    public static void userQuit(@NonNull Player player) {
        VanishPerms.users.remove(player.getName());
    }

    private static VanishUser getUser(@NonNull Player player) {
        VanishUser user = VanishPerms.users.get(player.getName());
        if (user == null) {
            user = new VanishUser(player);
            VanishPerms.users.put(player.getName(), user);
        }
        return user;
    }
}
