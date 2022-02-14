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
package org.kitteh.vanish.hooks.plugins;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.ChatProvider;
import org.kitteh.vanish.hooks.Hook;

public final class VaultHook extends Hook implements ChatProvider {
    private Chat chat;

    public VaultHook(@NonNull VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NonNull String getPrefix(@NonNull Player player) {
        return this.whatsInTheVault() ? this.getVaultPrefix(player) : "";
    }

    @Override
    public @NonNull String getSuffix(@NonNull Player player) {
        return this.whatsInTheVault() ? this.getVaultSuffix(player) : "";
    }

    private @NonNull String get(@Nullable String value) {
        return value != null ? value : "";
    }

    private @NonNull String getVaultPrefix(@NonNull Player player) {
        return this.get(this.chat.getPlayerPrefix(player));
    }

    private @NonNull String getVaultSuffix(@NonNull Player player) {
        return this.get(this.chat.getPlayerSuffix(player));
    }

    private void loadVault() {
        final RegisteredServiceProvider<Chat> chatProvider = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            this.chat = chatProvider.getProvider();
        }
    }

    private boolean whatsInTheVault() {
        if ((this.chat == null) && this.plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.loadVault();
        }
        return this.chat != null;
    }
}
