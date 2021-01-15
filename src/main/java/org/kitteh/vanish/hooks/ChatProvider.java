package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;

public interface ChatProvider {
    String getPrefix(Player player);

    String getSuffix(Player player);
}