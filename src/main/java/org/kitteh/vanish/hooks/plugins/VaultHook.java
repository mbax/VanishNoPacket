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
        return this.whatsInTheVault() ? this.getVaultPrefix(player) : "";
    }

    @Override
    public String getSuffix(Player player) {
        return this.whatsInTheVault() ? this.getVaultSuffix(player) : "";
    }

    private String get(String value) {
        return value != null ? value : "";
    }

    private String getVaultPrefix(Player player) {
        return this.get(this.chat.getPlayerPrefix(player));
    }

    private String getVaultSuffix(Player player) {
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