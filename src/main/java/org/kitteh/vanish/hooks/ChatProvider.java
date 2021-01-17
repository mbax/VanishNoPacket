package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ChatProvider {
    @NonNull String getPrefix(Player player);

    @NonNull String getSuffix(Player player);
}