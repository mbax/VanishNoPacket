package org.kitteh.vanish.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.kitteh.vanish.Debuggle;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.plugins.BPermissionsHook;
import org.kitteh.vanish.hooks.plugins.DisguiseCraftHook;
import org.kitteh.vanish.hooks.plugins.DynmapHook;
import org.kitteh.vanish.hooks.plugins.EssentialsHook;
import org.kitteh.vanish.hooks.plugins.GeoIPToolsHook;
import org.kitteh.vanish.hooks.plugins.JSONAPIHook;
import org.kitteh.vanish.hooks.plugins.ProtocolLibHook;
import org.kitteh.vanish.hooks.plugins.VaultHook;

public final class HookManager {
    public enum HookType {
        BPermissions(BPermissionsHook.class),
        DisguiseCraft(DisguiseCraftHook.class),
        Dynmap(DynmapHook.class),
        Essentials(EssentialsHook.class),
        GeoIPTools(GeoIPToolsHook.class),
        JSONAPI(JSONAPIHook.class),
        ProtocolLib(ProtocolLibHook.class),
        Vault(VaultHook.class);

        private Class<? extends Hook> clazz;

        HookType(Class<? extends Hook> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Hook> get() {
            return this.clazz;
        }
    }

    private final HashMap<String, Hook> hooks = new HashMap<String, Hook>();
    private final VanishPlugin plugin;

    public HookManager(VanishPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Deregisters a hook by object
     *
     * @param hook hook object to deregister
     * @return a list of deregistered hook names. Empty list if nothing deregistered.
     */
    public List<String> deregisterHook(Hook hook) {
        final List<String> ret = new ArrayList<String>();
        for (final Map.Entry<String, Hook> i : this.hooks.entrySet()) {
            if (i.getValue().equals(hook)) {
                this.deregisterHook(i.getKey());
                ret.add(i.getKey());
            }
        }
        return ret;
    }

    /**
     * Deregisters a hook
     *
     * @param name hook name to deregister
     * @return the deregistered hook or null if no hook by the given name was registered
     */
    public Hook deregisterHook(String name) {
        final Hook ret = this.hooks.get(name);
        this.hooks.remove(name);
        return ret;
    }

    /**
     * Gets a registered hook by HookType
     *
     * @param hooktype desired hook type
     * @return the named Hook if registered, null if no match.
     */
    public Hook getHook(HookType hooktype) {
        if (!this.hooks.containsKey(hooktype.name())) {
            this.registerHook(hooktype);
        }
        return this.hooks.get(hooktype.name());
    }

    /**
     * Gets a named, registered Hook
     *
     * @param name hook name
     * @return the named Hook if registered, null if no match.
     */
    public Hook getHook(String name) {
        return this.hooks.get(name);
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

    /**
     * Registers and initializes a hook
     *
     * @param name name to register
     * @param hookClazz hook class to register
     */
    public void registerHook(String name, Class<? extends Hook> hookClazz) {
        try {
            this.registerHook(name, hookClazz.getConstructor(VanishPlugin.class).newInstance(this.plugin));
        } catch (final Exception e) {
            Debuggle.log("Failed to add hook " + name);
            e.printStackTrace();
        }
    }

    /**
     * Registers an initialized hook object.
     *
     * @param name name of the hook
     * @param hook hook object
     */
    public void registerHook(String name, Hook hook) {
        this.hooks.put(name, hook);
    }

    private void registerHook(HookType hook) {
        this.registerHook(hook.name(), hook.get());
    }
}