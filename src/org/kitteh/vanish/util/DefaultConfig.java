package org.kitteh.vanish.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

/**
 * I'll remove this on next Bukkit RB, lol forced upgrades
 */
public class DefaultConfig {
    public static void set(String name) {
        try {
            final File target = new File(VanishNoPacket.getPlugin().getDataFolder(), name);
            final InputStream source = VanishNoPacket.getPlugin().getResource(name);
            if (source == null) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not find default config " + name);
                return;
            }
            if (!VanishNoPacket.getPlugin().getDataFolder().exists()) {
                VanishNoPacket.getPlugin().getDataFolder().mkdir();
            }
            if (!target.exists()) {
                final OutputStream output = new FileOutputStream(target);
                int len;
                final byte[] buffer = new byte[1024];
                while ((len = source.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.close();
            }
            source.close();
        } catch (final Exception ex) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not save default config to " + name, ex);
        }
    }
}
