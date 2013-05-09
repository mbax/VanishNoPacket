package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;

public interface ChatProvider {
    public String getPrefix(Player player);

    public String getSuffix(Player player);
}