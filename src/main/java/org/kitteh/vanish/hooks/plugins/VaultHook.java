package org.kitteh.vanish.hooks.plugins;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.ChatProvider;
import org.kitteh.vanish.hooks.Hook;

public final class VaultHook extends Hook implements ChatProvider {
    private Chat chat;

    public VaultHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getPrefix(Player player) {
        this.whatsInTheVault();
        String value = null;
        if (this.chat != null) {
            value = this.chat.getPlayerPrefix(player);
        }
        return value != null ? value : "";
    }

    @Override
    public String getSuffix(Player player) {
        this.whatsInTheVault();
        String value = null;
        if (this.chat != null) {
            value = this.chat.getPlayerSuffix(player);
        }
        return value != null ? value : "";
    }

    private void whatsInTheVault() {
        if (this.chat == null) {
            final RegisteredServiceProvider<Chat> chatProvider = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (chatProvider != null) {
                this.chat = chatProvider.getProvider();
            }
        }
    }
}