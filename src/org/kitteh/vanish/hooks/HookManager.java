package org.kitteh.vanish.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final HashMap<String, Hook> hooks;

    public HookManager(VanishPlugin plugin) {
        this.hooks = new HashMap<String, Hook>();
        for (final HookType hook : HookType.values()) {
            registerHook(hook, plugin);
        }
    }

    public void registerHook(String name, Hook hook){
    	this.hooks.put(name, hook);
    }
    
    public Hook deregisterHook(String name){
    	Hook ret = this.hooks.get(name);
    	this.hooks.remove(name);
    	return ret;
    }
    
    public List<String> deregisterHook(Hook hook){
    	List<String> ret = new ArrayList<String>();
    	for(Map.Entry<String, Hook> i : hooks.entrySet()){
    		if(i.getValue().equals(hook)){
    			deregisterHook(i.getKey());
    			ret.add(i.getKey());
    		}
    	}
    	return ret;
    }

    public void registerHook(String name, Class<? extends Hook> hookClazz, VanishPlugin plugin){
    	try {
            registerHook(name, hookClazz.getConstructor(VanishPlugin.class).newInstance(plugin));
        } catch (final Exception e) {
            Debuggle.log("Failed to add hook " + name);
            e.printStackTrace();
        }
    }

    public void registerHook(HookType hook, VanishPlugin plugin){
    	registerHook(hook.name(), hook.get(), plugin);
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
