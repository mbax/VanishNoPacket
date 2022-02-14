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
package org.kitteh.vanish.hooks;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kitteh.vanish.Debuggle;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.plugins.DiscordSRVHook;
import org.kitteh.vanish.hooks.plugins.DynmapHook;
import org.kitteh.vanish.hooks.plugins.EssentialsHook;
import org.kitteh.vanish.hooks.plugins.SquaremapHook;
import org.kitteh.vanish.hooks.plugins.VaultHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HookManager {
    public enum HookType {
        Dynmap(DynmapHook.class),
        Essentials(EssentialsHook.class),
        Vault(VaultHook.class),
        DiscordSRV(DiscordSRVHook.class),
        squaremap(SquaremapHook.class);

        private final Class<? extends Hook> clazz;

        HookType(Class<? extends Hook> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Hook> get() {
            return this.clazz;
        }
    }

    private final HashMap<String, Hook> hooks = new HashMap<>();
    private final VanishPlugin plugin;

    public HookManager(@NonNull VanishPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Deregisters a hook by object
     *
     * @param hook hook object to deregister
     * @return a list of deregistered hook names. Empty list if nothing deregistered.
     */
    public @NonNull List<String> deregisterHook(@NonNull Hook hook) {
        final List<String> ret = new ArrayList<>();
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
    @SuppressWarnings("UnusedReturnValue")
    public @Nullable Hook deregisterHook(@NonNull String name) {
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
    public @NonNull Hook getHook(@NonNull HookType hooktype) {
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
    public @Nullable Hook getHook(@NonNull String name) {
        return this.hooks.get(name);
    }

    public void onDisable() {
        for (final Hook hook : this.hooks.values()) {
            hook.onDisable();
        }
    }

    public void onJoin(@NonNull Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onJoin(player);
        }
    }

    public void onQuit(@NonNull Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onQuit(player);
        }
    }

    public void onUnvanish(@NonNull Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onUnvanish(player);
        }
    }

    public void onVanish(@NonNull Player player) {
        for (final Hook hook : this.hooks.values()) {
            hook.onVanish(player);
        }
    }

    public void onFakeJoin(@NonNull Player player) {
        for(final Hook hook : this.hooks.values()) {
            hook.onFakeJoin(player);
        }
    }

    public void onFakeQuit(@NonNull Player player) {
        for(final Hook hook : this.hooks.values()) {
            hook.onFakeQuit(player);
        }
    }

    /**
     * Registers and initializes a hook
     *
     * @param name name to register
     * @param hookClazz hook class to register
     */
    public void registerHook(@NonNull String name, @NonNull Class<? extends Hook> hookClazz) {
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
    public void registerHook(@NonNull String name, @NonNull Hook hook) {
        this.hooks.put(name, hook);
    }

    private void registerHook(@NonNull HookType hook) {
        this.registerHook(hook.name(), hook.get());
    }
}
