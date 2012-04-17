package org.kitteh.vanish.hooks;

import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import com.alecgorge.minecraft.jsonapi.JSONAPI;

public class JSONAPIHook {
    private final VanishPlugin plugin;
    private boolean enabled;

    public JSONAPIHook(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPluginEnable(boolean enableJSONAPI) {
        this.enabled = enableJSONAPI;
        if (this.enabled) {
            final JSONAPI plug = this.grabJSONAPI();
            if (plug != null) {
                plug.registerMethod("{" + "   \"name\": \"isVanished\"," + "   \"desc\": \"Checks if named player is vanished\"," + "   \"call\": \"Plugins.VanishNoPacket.getManager().isVanished(0)\"," + "   \"returns\": [\"boolean\", \"True if player is invisible. False if offline or visible.\"]," + "   \"args\": [" + "       [\"String\", \"Player's name\"]" + "   ]" + "}");
            }
        }
    }

    private JSONAPI grabJSONAPI() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("JSONAPI");
        final JSONAPI toReturn;
        if (grab != null) {
            toReturn = ((JSONAPI) grab);
            this.plugin.log("Now hooking into JSONAPI");
        } else {
            this.plugin.log("You wanted JSONAPI support. I could not find JSONAPI.");
            toReturn = null;
            this.enabled = false;
        }
        return toReturn;
    }
}
