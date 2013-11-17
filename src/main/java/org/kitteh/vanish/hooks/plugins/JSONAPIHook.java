package org.kitteh.vanish.hooks.plugins;

import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

import com.alecgorge.minecraft.jsonapi.JSONAPI;

public final class JSONAPIHook extends Hook {
    public JSONAPIHook(VanishPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("JSONAPI");
        if ((grab != null) && (grab instanceof JSONAPI)) {
            ((JSONAPI) grab).registerMethod("{   \"name\": \"isVanished\",   \"desc\": \"Checks if named player is vanished\",   \"call\": \"Plugins.VanishNoPacket.getManager().isVanished(0)\",  \"returns\": [\"boolean\", \"True if player is invisible. False if offline or visible.\"],   \"args\": [       [\"String\", \"Player's name\"]   ]}");
            ((JSONAPI) grab).registerMethod("{   \"name\": \"getOnlineStatus\",   \"desc\": \"Check if player is considered online\",   \"call\": \"Plugins.VanishNoPacket.getManager().getAnnounceManipulator().getFakeOnlineStatus(0)\",  \"returns\": [\"boolean\", \"True if player is considered online. False if not or player offline.\"],   \"args\": [       [\"String\", \"Player's name\"]   ]}");
            this.plugin.getLogger().info("Now hooking into JSONAPI");
        } else {
            this.plugin.getLogger().info("You wanted JSONAPI support. I could not find JSONAPI.");
            return;
        }
    }
}