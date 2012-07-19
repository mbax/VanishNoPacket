package org.kitteh.vanish.injector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet20NamedEntitySpawn;

@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
public class ArrayLizt extends ArrayList {
    private static Field syncField;
    private static Field highField;
    private static boolean go = true;
    private static boolean enabled = false;

    static {
        try {
            ArrayLizt.syncField = NetworkManager.class.getDeclaredField("g");
            ArrayLizt.syncField.setAccessible(true);
            ArrayLizt.highField = NetworkManager.class.getDeclaredField("highPriorityQueue");
            ArrayLizt.highField.setAccessible(true);
        } catch (final Exception e) {
            e.printStackTrace();
            ArrayLizt.go = false;
        }
    }

    public static void disable() {
        ArrayLizt.enabled = false;
    }

    public static void enable() {
        ArrayLizt.enabled = true;
    }

    public static void inject(Player player) {
        if (!ArrayLizt.go || !ArrayLizt.enabled) {
            return;
        }
        final NetworkManager nm = ArrayLizt.getManager(player);
        try {
            ArrayLizt.nom(nm, Collections.synchronizedList(new ArrayLizt()));
        } catch (final Exception e) {
            System.out.println("[Vanish] Failed to inject into networkmanager. No colors 4 u.");
            e.printStackTrace();
            ArrayLizt.go = false;
        }
    }

    public static void outject(Player player) {
        if (!ArrayLizt.enabled) {
            return;
        }
        final NetworkManager nm = ArrayLizt.getManager(player);
        try {
            ArrayLizt.nom(nm, Collections.synchronizedList(new ArrayList()));
        } catch (final Exception e) {
            System.out.println("[Vanish] Failed to update networkmanager on disable. Could be a problem.");
            e.printStackTrace();
        }
    }

    private static NetworkManager getManager(Player player) {
        return ((CraftPlayer) player).getHandle().netServerHandler.networkManager;
    }

    private static void nom(NetworkManager nm, List list) throws IllegalArgumentException, IllegalAccessException {
        final List old = (List) ArrayLizt.highField.get(nm);
        synchronized (ArrayLizt.syncField.get(nm)) {
            for (final Object object : old) {
                list.add(object);
            }
            ArrayLizt.highField.set(nm, list);
        }
    }

    @Override
    public boolean add(Object o) {
        if (o instanceof Packet20NamedEntitySpawn) {
            try {
                final Packet20NamedEntitySpawn packet = ((Packet20NamedEntitySpawn) o);
                String name = packet.b;
                if (VanishNoPacket.isVanished(name)) {
                    name = "\u00A7" + "b" + name;
                    if (name.length() > 16) {
                        name = name.substring(0, 16);
                    }
                    packet.b = name;
                }
            } catch (final VanishNotLoadedException e) {
            }
        }
        return super.add(o);
    }
}
