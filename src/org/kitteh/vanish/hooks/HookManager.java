package org.kitteh.vanish.hooks;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.kitteh.vanish.Debuggle;
import org.kitteh.vanish.VanishPlugin;

public class HookManager {
    public enum HookType {
        BPermissions(BPermissionsHook.class), // bperms!
        Dynmap(DynmapHook.class), // dynmap!
        Essentials(EssentialsHook.class), // essentials! 
        GeoIPTools(GeoIPToolsHook.class), // geoiptools!
        JSONAPI(JSONAPIHook.class), // jsonapi!
        SpoutCraft(SpoutCraftHook.class); // spoutcraft!

        private Class<? extends Hook> clazz;

        HookType(Class<? extends Hook> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Hook> get() {
            return this.clazz;
        }
    }

    private final HashMap<HookType, Hook> hooks;

    public HookManager(VanishPlugin plugin) {
        this.hooks = new HashMap<HookType, Hook>();
        for (final HookType hook : HookType.values()) {
            try {
                this.hooks.put(hook, hook.get().getConstructor(VanishPlugin.class).newInstance(plugin));
            } catch (final Exception e) {
                Debuggle.log("Failed to hook " + hook);
                e.printStackTrace();
            }
        }
    }

    public Hook getHook(HookType hooktype) {
        return this.hooks.get(hooktype);
    }

    public void onDisable() {
        for (final Hook hook : this.hooks.values()) {
            hook.onDisable();
        }
    }

    public void onJoin(Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onJoin(player);
        }
    }

    public void onQuit(Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onQuit(player);
        }
    }

    public void onUnvanish(Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onUnvanish(player);
        }
    }

    public void onVanish(Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onVanish(player);
        }
    }
}
